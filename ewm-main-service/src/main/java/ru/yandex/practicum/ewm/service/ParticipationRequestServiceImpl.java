package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.EventState;
import ru.yandex.practicum.ewm.model.ParticipationRequest;
import ru.yandex.practicum.ewm.model.ParticipationRequestStatus;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.ParticipationRequestRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;
import ru.yandex.practicum.ewm.validator.ConflictException;
import ru.yandex.practicum.ewm.validator.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Requester with id %d does not exist", userId)));

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id %d does not exist", eventId)));

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException(String.format(
                    "Request with requester id %d and id %d exists", userId, eventId));
        }

        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("Event with initiator id %d and id %d exists", userId, eventId));
        }

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("You can't participate in an unpublished event");
        }

        long currentConfirmedRequests = requestRepository.countByEventIdAndStatus(
                eventId, ParticipationRequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && currentConfirmedRequests == event.getParticipantLimit()) {
            throw new ConflictException("Event request limit reached");
        }

        ParticipationRequest request = new ParticipationRequest();

        request.setEvent(event);
        request.setRequester(user);

        ParticipationRequestStatus status = ParticipationRequestStatus.PENDING;
        if (event.getParticipantLimit() == 0 || Boolean.FALSE.equals(event.getRequestModeration())) {
            status = ParticipationRequestStatus.CONFIRMED;
        }

        request.setStatus(status);
        request.setCreated(LocalDateTime.now());

        return ParticipationRequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long id) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Requester with id %d does not exist", userId));
        }

        ParticipationRequest request = requestRepository.findByIdAndRequesterId(id, userId).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Participation request with id %d and requester id %d does not exist", id, userId)));

        if (!eventRepository.existsById(request.getEvent().getId())) {
            throw new NotFoundException(String.format("Event with id %d does not exist", request.getEvent().getId()));
        }

        request.setStatus(ParticipationRequestStatus.CANCELED);

        return ParticipationRequestMapper.toDto(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Requester with id %d does not exist", userId));
        }

        return ParticipationRequestMapper.toDtos(requestRepository.findByRequesterId(userId));
    }
}
