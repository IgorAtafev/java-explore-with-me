package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.EventFullForRequestDto;
import ru.yandex.practicum.ewm.dto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.ewm.dto.EventShortDto;
import ru.yandex.practicum.ewm.dto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.dto.ViewStatsDto;
import ru.yandex.practicum.ewm.mapper.EventMapper;
import ru.yandex.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.EventSortType;
import ru.yandex.practicum.ewm.model.EventState;
import ru.yandex.practicum.ewm.model.EventStateAction;
import ru.yandex.practicum.ewm.model.ParticipationRequest;
import ru.yandex.practicum.ewm.model.ParticipationRequestStatus;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.CategoryRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.ParticipationRequestRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;
import ru.yandex.practicum.ewm.specification.EventSpecification;
import ru.yandex.practicum.ewm.util.EventRequestParam;
import ru.yandex.practicum.ewm.util.StatsRequestParam;
import ru.yandex.practicum.ewm.validator.ConflictException;
import ru.yandex.practicum.ewm.validator.NotFoundException;
import ru.yandex.practicum.ewm.validator.ValidationException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements  EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;
    private final StatsService statsService;

    @Override
    public EventFullDto createEvent(Long userId, EventFullForRequestDto eventDto) {
        EventState state = EventState.PENDING;

        if (eventDto.getShortDto().getPaid() == null) {
            eventDto.getShortDto().setPaid(Boolean.FALSE);
        }
        if (eventDto.getParticipantLimit() == null) {
            eventDto.setParticipantLimit(0);
        }
        if (eventDto.getRequestModeration() == null) {
            eventDto.setRequestModeration(Boolean.TRUE);
        }

        Event event = toEvent(userId, eventDto, state, null);

        checkEventDateByCurrentDate(event.getEventDate());

        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateUserEvent(Long userId, Long id, EventFullForRequestDto eventDto) {
        eventDto.getShortDto().setId(id);

        Event oldEvent = eventRepository.findByIdAndInitiatorId(eventDto.getShortDto().getId(), userId).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Event with id %d and initiator id %d does not exist", eventDto.getShortDto().getId(),
                        userId)));

        if (EventState.PUBLISHED.equals(oldEvent.getState())) {
            throw new ConflictException("You can't edit a published event");
        }

        EventState state = getEventState(eventDto, oldEvent,
                Set.of(EventStateAction.SEND_TO_REVIEW, EventStateAction.CANCEL_REVIEW));
        checkEventState(state, oldEvent);

        Event event = toEvent(userId, eventDto, state, oldEvent);

        checkEventDateByCurrentDate(event.getEventDate());

        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEventByAdmin(Long id, EventFullForRequestDto eventDto) {
        eventDto.getShortDto().setId(id);

        Event oldEvent = eventRepository.findById(eventDto.getShortDto().getId()).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Event with id %d does not exist", eventDto.getShortDto().getId())));

        EventState state = getEventState(eventDto, oldEvent,
                Set.of(EventStateAction.PUBLISH_EVENT, EventStateAction.REJECT_EVENT));
        checkEventState(state, oldEvent);

        Event event = toEvent(oldEvent.getInitiator().getId(), eventDto, state, oldEvent);

        checkEventDateByCurrentDate(event.getEventDate());
        if (event.getPublishedOn() != null) {
            checkEventDateByPublishDate(event.getEventDate(), event.getPublishedOn());
        }

        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getUserEvents(Long userId, Pageable page) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Initiator with id %d does not exist", userId));
        }

        List<Event> events = eventRepository.findByInitiatorId(userId, page);
        setConfirmedRequestsForEvents(events);

        return EventMapper.toShortDtos(events);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getUserEventById(Long userId, Long id) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Initiator with id %d does not exist", userId));
        }

        Event event = eventRepository.findByIdAndInitiatorId(id, userId).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Event with id %d and initiator id %d does not exist", id, userId)));

        event.setConfirmedRequests((int)requestRepository.countByEventIdAndStatus(
                id, ParticipationRequestStatus.CONFIRMED));

        return EventMapper.toFullDto(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getEventsByAdmin(EventRequestParam requestParam, Pageable page) {
        if (requestParam.getRangeStart() != null && requestParam.getRangeEnd() != null) {
            checkRangeStartBeforeRangeEnd(requestParam.getRangeStart(), requestParam.getRangeEnd());
        }

        List<EventState> eventStates = null;
        if (requestParam.getStates() != null) {
            eventStates = getEventStates(requestParam.getStates());
        }

        List<Specification> conditions = new ArrayList<>();

        if (requestParam.getUsers() != null) {
            conditions.add(EventSpecification.findByInitiatorIdIn(requestParam.getUsers()));
        }

        if (eventStates != null) {
            conditions.add(EventSpecification.findByStatesIn(eventStates));
        }

        if (requestParam.getCategories() != null) {
            conditions.add(EventSpecification.findByCategoryIdIn(requestParam.getCategories()));
        }

        if (requestParam.getRangeStart() != null) {
            conditions.add(EventSpecification.findByRangeStartGreaterThanEqual(requestParam.getRangeStart()));
        }
        if (requestParam.getRangeEnd() != null) {
            conditions.add(EventSpecification.findByRangeEndLessThanEqual(requestParam.getRangeEnd()));
        }

        List<Event> events = getEventsByCondition(conditions, page);

        return EventMapper.toFullDtos(events);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(
            Long userId, Long id, EventRequestStatusUpdateRequest requestsDto
    ) {
        checkParticipationRequestStatus(requestsDto.getStatus());

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Initiator with id %d does not exist", userId));
        }

        Event event = eventRepository.findByIdAndInitiatorId(id, userId).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Event with id %d and initiator id %d does not exist", id, userId)));

        long currentConfirmedRequests = requestRepository.countByEventIdAndStatus(
                id, ParticipationRequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && currentConfirmedRequests == event.getParticipantLimit()) {
            throw new ConflictException("Event request limit reached");
        }

        List<ParticipationRequest> requests = requestRepository.findByIdInAndEventIdAndStatus(
                requestsDto.getRequestIds(), id, ParticipationRequestStatus.PENDING);

        if (requestsDto.getRequestIds().size() != requests.size()) {
            throw new ConflictException("The status can only be changed for participations in the pending state");
        }

        if (ParticipationRequestStatus.CONFIRMED.equals(requestsDto.getStatus())
                && (event.getParticipantLimit() == 0
                    || Boolean.FALSE.equals(event.getRequestModeration()))) {
            return new EventRequestStatusUpdateResult();
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            ParticipationRequestStatus currentStatus = requestsDto.getStatus();

            if (ParticipationRequestStatus.CONFIRMED.equals(requestsDto.getStatus())) {
                if (event.getParticipantLimit() > currentConfirmedRequests) {
                    currentConfirmedRequests++;
                } else {
                    currentStatus = ParticipationRequestStatus.REJECTED;
                }
            }

            request.setStatus(currentStatus);

            ParticipationRequestDto requestDto = ParticipationRequestMapper.toDto(request);

            if (ParticipationRequestStatus.CONFIRMED.equals(currentStatus)) {
                confirmedRequests.add(requestDto);
            } else {
                rejectedRequests.add(requestDto);
            }
        }

        requestRepository.saveAll(requests);

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long id) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Initiator with id %d does not exist", userId));
        }

        if (!eventRepository.existsByIdAndInitiatorId(id, userId)) {
            throw new NotFoundException(String.format(
                    "Event with id %d and initiator id %d does not exist", id, userId));
        }

        return ParticipationRequestMapper.toDtos(requestRepository.findByEventId(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getPublicEvents(
            EventRequestParam requestParam, HttpServletRequest request, Pageable page
    ) {
        if (requestParam.getRangeStart() != null && requestParam.getRangeEnd() != null) {
            checkRangeStartBeforeRangeEnd(requestParam.getRangeStart(), requestParam.getRangeEnd());
        }

        EventSortType sortType = getSortType(requestParam.getSort());

        List<Specification> conditions = new ArrayList<>();

        conditions.add(EventSpecification.isPublished());

        if (requestParam.getText() != null) {
            conditions.add(EventSpecification.findByTextContaining(requestParam.getText()));
        }

        if (requestParam.getCategories() != null) {
            conditions.add(EventSpecification.findByCategoryIdIn(requestParam.getCategories()));
        }

        if (requestParam.getPaid() != null) {
            conditions.add(EventSpecification.findByPaid(requestParam.getPaid()));
        }

        if (requestParam.getOnlyAvailable() != null && Boolean.TRUE.equals(requestParam.getOnlyAvailable())) {
            conditions.add(EventSpecification.findByOnlyAvailable());
        }

        if (requestParam.getRangeStart() != null) {
            conditions.add(EventSpecification.findByRangeStartGreaterThanEqual(requestParam.getRangeStart()));
        }
        if (requestParam.getRangeEnd() != null) {
            conditions.add(EventSpecification.findByRangeEndLessThanEqual(requestParam.getRangeEnd()));
        }

        List<Event> events = getEventsByCondition(conditions, page);

        statsService.saveEndpointHit(request);
        setViewForEvents(events, request);

        if (EventSortType.VIEWS.getType().equals(requestParam.getSort())) {
            events.stream()
                    .sorted(Comparator.comparing(Event::getViews))
                    .collect(Collectors.toList());
        }

        return EventMapper.toShortDtos(events);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getPublicEventById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Public event with id %d does not exist", id)));

        event.setConfirmedRequests((int)requestRepository.countByEventIdAndStatus(
                id, ParticipationRequestStatus.CONFIRMED));

        statsService.saveEndpointHit(request);
        event.setViews(getViewForEvent(event.getPublishedOn(), request));

        return EventMapper.toFullDto(event);
    }

    private Event toEvent(Long userId, EventFullForRequestDto eventDto, EventState state, Event oldEvent) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Initiator with id %d does not exist", userId)));

        Category category = null;
        if (eventDto.getShortDto().getCategoryId() != null) {
            category = categoryRepository.findById(eventDto.getShortDto().getCategoryId()).orElseThrow(
                    () -> new NotFoundException(
                            String.format("Category with id %d does not exist", eventDto.getShortDto().getCategoryId())
                    ));
        }

        LocalDateTime created = LocalDateTime.now();

        Event event = EventMapper.toEvent(eventDto);

        if (eventDto.getShortDto().getId() != null) {
            if (eventDto.getShortDto().getTitle() == null) {
                event.setTitle(oldEvent.getTitle());
            }

            if (eventDto.getShortDto().getAnnotation() == null) {
                event.setAnnotation(oldEvent.getAnnotation());
            }

            if (eventDto.getDescription() == null) {
                event.setDescription(oldEvent.getDescription());
            }

            if (eventDto.getLocation() == null) {
                event.setLocation(oldEvent.getLocation());
            }

            if (eventDto.getShortDto().getEventDate() == null) {
                event.setEventDate(oldEvent.getEventDate());
            }

            if (eventDto.getShortDto().getPaid() == null) {
                event.setPaid(oldEvent.getPaid());
            }

            if (eventDto.getParticipantLimit() == null) {
                event.setParticipantLimit(oldEvent.getParticipantLimit());
            }

            if (eventDto.getRequestModeration() == null) {
                event.setRequestModeration(oldEvent.getRequestModeration());
            }

            if (eventDto.getShortDto().getCategoryId() == null) {
                category = oldEvent.getCategory();
            }

            created = oldEvent.getCreated();
        }

        if (EventState.PUBLISHED.equals(state)) {
            event.setPublishedOn(LocalDateTime.now());
        }

        event.setInitiator(user);
        event.setCategory(category);
        event.setState(state);
        event.setCreated(created);

        return event;
    }

    private EventState getEventState(EventFullForRequestDto eventDto, Event event, Set<EventStateAction> stateActions) {
        if (eventDto.getStateAction() == null) {
            return event.getState();
        } else if (stateActions.contains(eventDto.getStateAction())) {
            return eventDto.getStateAction().getState();
        } else {
            throw new ConflictException("Unknown event state action: UNSUPPORTED_STATUS");
        }
    }

    private void checkEventState(EventState state, Event event) {
        if (EventState.PUBLISHED.equals(state) && !EventState.PENDING.equals(event.getState())) {
            throw new ConflictException("An event can only be published if it is in the publish pending state");
        }

        if (EventState.CANCELED.equals(state) && EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("An event can only be canceled if it is not published");
        }
    }

    private void checkEventDateByCurrentDate(LocalDateTime eventDate) {
        LocalDateTime now = LocalDateTime.now();

        if (eventDate.isBefore(now.plusHours(2L))) {
            throw new ConflictException(
                    String.format("%tc cannot be earlier than two hours from the current moment", eventDate)
            );
        }
    }

    private void checkEventDateByPublishDate(LocalDateTime eventDate, LocalDateTime publishedOn) {
        if (eventDate.isBefore(publishedOn.plusHours(1L))) {
            throw new ConflictException(
                    String.format("%tc cannot be earlier than one hour from the publishing date", eventDate)
            );
        }
    }

    private void checkRangeStartBeforeRangeEnd(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException(String.format("The start of the range must be before the end of the range"));
        }
    }

    private List<EventState> getEventStates(List<String> states) {
        try {
            return states.stream()
                    .map(state -> state.toUpperCase())
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown event state: UNSUPPORTED_STATUS");
        }
    }

    private void checkParticipationRequestStatus(ParticipationRequestStatus status) {
        if (!Set.of(ParticipationRequestStatus.CONFIRMED, ParticipationRequestStatus.REJECTED)
                .contains(status)
        ) {
            throw new ValidationException("Unknown participation request status: UNSUPPORTED_STATUS");
        }
    }

    private void setConfirmedRequestsForEvents(List<Event> events) {
        if (events.isEmpty()) {
            return;
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, List<ParticipationRequest>> confirmedRequests = requestRepository.findByEventIdInAndStatus(
                eventIds, ParticipationRequestStatus.CONFIRMED).stream()
                .collect(Collectors.groupingBy(item -> item.getEvent().getId()));

        events.forEach(event -> setConfirmedRequestsForEvent(event, confirmedRequests.get(event.getId())));
    }

    private void setConfirmedRequestsForEvent(Event event, List<ParticipationRequest> requests) {
        if (requests != null) {
            event.setConfirmedRequests(requests.size());
        }
    }

    private List<Event> getEventsByCondition(List<Specification> conditions, Pageable page) {
        List<Event> events;

        if (!conditions.isEmpty()) {
            conditions.set(0, Specification.where(conditions.get(0)));
            Specification finalCondition = conditions.stream()
                    .reduce(Specification::and)
                    .get();

            events = eventRepository.findAll(finalCondition, page).toList();
        } else {
            events = eventRepository.findAll(page).toList();
        }

        setConfirmedRequestsForEvents(events);

        return events;
    }

    private EventSortType getSortType(String sort) {
        try {
            return EventSortType.valueOf(sort.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown sort type: UNSUPPORTED_STATUS");
        }
    }

    private Long getViewForEvent(LocalDateTime start, HttpServletRequest request) {
        StatsRequestParam requestParam = StatsRequestParam.builder()
                .start(start)
                .end(LocalDateTime.now())
                .uris(List.of(request.getRequestURI()))
                .unique(true)
                .build();

        List<ViewStatsDto> stats = statsService.getStats(requestParam);

        if (stats.isEmpty()) {
            return 0L;
        }

        return stats.get(0).getHits();
    }

    private void setViewForEvents(List<Event> events, HttpServletRequest request) {
        List<String> uris = new ArrayList<>();

        LocalDateTime minPublishedOn = events.get(0).getPublishedOn();
        for (Event event : events) {
            uris.add(request.getRequestURI() + event.getId());

            if (minPublishedOn.isAfter(event.getPublishedOn())) {
                minPublishedOn = event.getPublishedOn();
            }
        }

        StatsRequestParam requestParam = StatsRequestParam.builder()
                .start(minPublishedOn)
                .end(LocalDateTime.now())
                .uris(uris)
                .unique(true)
                .build();

        Map<String, List<ViewStatsDto>> stats = statsService.getStats(requestParam).stream()
                .collect(Collectors.groupingBy(endPoint -> endPoint.getUri()));

        if (stats.isEmpty()) {
            return;
        }

        for (Event event : events) {
            List<ViewStatsDto> hits = stats.get(request.getRequestURI() + event.getId());
            if (hits == null) {
                continue;
            }

            event.setViews(hits.get(0).getHits());
        }
    }
}
