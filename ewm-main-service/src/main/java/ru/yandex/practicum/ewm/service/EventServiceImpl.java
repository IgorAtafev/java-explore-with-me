package ru.yandex.practicum.ewm.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventRequestDto;
import ru.yandex.practicum.ewm.dto.EventShortDto;
import ru.yandex.practicum.ewm.mapper.EventMapper;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.EventState;
import ru.yandex.practicum.ewm.model.StateAction;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.CategoryRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;
import ru.yandex.practicum.ewm.validator.ConflictException;
import ru.yandex.practicum.ewm.validator.NotFoundException;
import ru.yandex.practicum.ewm.validator.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.ewm.model.QEvent.event;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements  EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private final EventMapper eventMapper;

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, EventRequestDto eventRequestDto) {
        EventState state = EventState.PENDING;

        if (eventRequestDto.getPaid() == null) {
            eventRequestDto.setPaid(Boolean.FALSE);
        }
        if (eventRequestDto.getParticipantLimit() == null) {
            eventRequestDto.setParticipantLimit(0);
        }
        if (eventRequestDto.getRequestModeration() == null) {
            eventRequestDto.setRequestModeration(Boolean.TRUE);
        }

        Event event = toEvent(userId, eventRequestDto, state, null);

        checkEventDateByCurrentDate(event.getEventDate());

        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Transactional
    @Override
    public EventFullDto updateUserEvent(Long userId, Long id, EventRequestDto eventRequestDto) {
        eventRequestDto.setId(id);

        Event oldEvent = eventRepository.findByInitiatorIdAndId(userId, eventRequestDto.getId()).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Event with initiator id %d and id %d does not exist", userId, eventRequestDto.getId())));

        if (oldEvent.getState() == EventState.PUBLISHED) {
            throw new ConflictException("You can't edit a published event");
        }

        Set<StateAction> stateActions = Set.of(StateAction.SEND_TO_REVIEW, StateAction.CANCEL_REVIEW);
        EventState state = getStateAction(eventRequestDto, oldEvent, stateActions);
        checkStateAction(state, oldEvent);

        Event event = toEvent(userId, eventRequestDto, state, oldEvent);

        checkEventDateByCurrentDate(event.getEventDate());

        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long id, EventRequestDto eventRequestDto) {
        eventRequestDto.setId(id);

        Event oldEvent = eventRepository.findById(eventRequestDto.getId()).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Event with id %d does not exist", eventRequestDto.getId())));

        Set<StateAction> stateActions = Set.of(StateAction.PUBLISH_EVENT, StateAction.REJECT_EVENT);
        EventState state = getStateAction(eventRequestDto, oldEvent, stateActions);
        checkStateAction(state, oldEvent);

        Event event = toEvent(oldEvent.getInitiator().getId(), eventRequestDto, state, oldEvent);

        checkEventDateByCurrentDate(event.getEventDate());
        if (event.getPublishedOn() != null) {
            checkEventDateByPublishDate(event.getEventDate(), event.getPublishedOn());
        }

        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getUserEvents(Long userId, Pageable page) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Initiator with id %d does not exist", userId));
        }

        return eventMapper.toShortDtos(eventRepository.findByInitiatorId(userId, page));
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getUserEventById(Long userId, Long id) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Initiator with id %d does not exist", userId));
        }

        Event event = eventRepository.findByInitiatorIdAndId(userId, id).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Event with initiator id %d and id %d does not exist", userId, id)));

        return eventMapper.toFullDto(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getEventsByAdmin(
            List<Long> users, List<String> states, List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page
    ) {
        if (rangeStart != null && rangeEnd != null) {
            checkRangeStartBeforeRangeEnd(rangeStart, rangeEnd);
        }

        List<EventState> eventStates = null;
        if (states != null) {
            eventStates = getEventStates(states);
        }

        List<BooleanExpression> conditions = new ArrayList<>();

        if (users != null) {
            conditions.add(event.initiator.id.in(users));
        }

        if (eventStates != null) {
            conditions.add(event.state.in(eventStates));
        }

        if (categories != null) {
            conditions.add(event.category.id.in(categories));
        }

        List<Event> events;

        BooleanExpression finalCondition = conditions.stream()
                    .reduce(BooleanExpression::and)
                    .orElse(null);

        if (finalCondition != null) {
            events = eventRepository.findAll(finalCondition, page).toList();
        } else {
            events = eventRepository.findAll(page).toList();
        }

        return eventMapper.toFullDtos(events);
    }

    private Event toEvent(Long userId, EventRequestDto eventRequestDto, EventState state, Event oldEvent) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Initiator with id %d does not exist", userId)));

        Category category = null;
        if (eventRequestDto.getCategoryId() != null) {
            category = categoryRepository.findById(eventRequestDto.getCategoryId()).orElseThrow(
                    () -> new NotFoundException(
                            String.format("Category with id %d does not exist", eventRequestDto.getCategoryId())
                    ));
        }

        Integer confirmedRequests = 0;
        LocalDateTime createdOn = LocalDateTime.now();

        Event event = eventMapper.toEvent(eventRequestDto);

        if (eventRequestDto.getId() != null) {
            if (eventRequestDto.getTitle() == null) {
                event.setTitle(oldEvent.getTitle());
            }

            if (eventRequestDto.getAnnotation() == null) {
                event.setAnnotation(oldEvent.getAnnotation());
            }

            if (eventRequestDto.getDescription() == null) {
                event.setDescription(oldEvent.getDescription());
            }

            if (eventRequestDto.getLocation() == null) {
                event.setLocation(oldEvent.getLocation());
            }

            if (eventRequestDto.getEventDate() == null) {
                event.setEventDate(oldEvent.getEventDate());
            }

            if (eventRequestDto.getPaid() == null) {
                event.setPaid(oldEvent.getPaid());
            }

            if (eventRequestDto.getParticipantLimit() == null) {
                event.setParticipantLimit(oldEvent.getParticipantLimit());
            }

            if (eventRequestDto.getRequestModeration() == null) {
                event.setRequestModeration(oldEvent.getRequestModeration());
            }

            if (eventRequestDto.getCategoryId() == null) {
                category = oldEvent.getCategory();
            }

            confirmedRequests = oldEvent.getConfirmedRequests();
            createdOn = oldEvent.getCreatedOn();
        }

        if (EventState.PUBLISHED.equals(state)) {
            event.setPublishedOn(LocalDateTime.now());
        }

        event.setInitiator(user);
        event.setCategory(category);
        event.setState(state);
        event.setConfirmedRequests(confirmedRequests);
        event.setCreatedOn(createdOn);

        return event;
    }

    private EventState getStateAction(EventRequestDto eventRequestDto, Event event, Set<StateAction> stateActions) {
        if (eventRequestDto.getStateAction() == null) {
            return event.getState();
        } else if (stateActions.contains(eventRequestDto.getStateAction())) {
            return eventRequestDto.getStateAction().getState();
        } else {
            throw new ConflictException("Unknown state action: UNSUPPORTED_STATUS");
        }
    }

    private void checkStateAction(EventState state, Event event) {
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
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown event state: UNSUPPORTED_STATUS");
        }
    }
}
