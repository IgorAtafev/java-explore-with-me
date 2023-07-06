package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.ewm.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {

    /**
     * Creates a new participation request
     * If the requester is not found throws NotFoundException
     * If the event is not found throws NotFoundException
     * If the user has previously created an event request throws ConflictException
     * If the user is the initiator of the event throws ConflictException
     * If the event is not published throws ConflictException
     * If the event request limit has been reached throws ConflictException
     *
     * @param userId
     * @param eventId
     * @return new participation request
     */
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    /**
     * Cancels a request to participate in an event
     * If the requester is not found throws NotFoundException
     * If the event is not found throws NotFoundException
     * If the request is not found throws NotFoundException
     *
     * @param userId
     * @param id
     * @return canceled participation request
     */
    ParticipationRequestDto cancelRequest(Long userId, Long id);

    /**
     * Returns a list of user participation requests
     * If the requester is not found throws NotFoundException
     *
     * @param userId
     * @return list of user participation requests
     */
    List<ParticipationRequestDto> getUserRequests(Long userId);

    /**
     * Updates the status of event requests
     * If the initiator is not found throws NotFoundException
     * If the event is not found throws NotFoundException
     * If the status of requests is not confirmed and not rejected throws ValidationException
     * If the event request limit is reached throws ConflictException
     * If requests are not pending throws ConflictException
     * If, upon confirmation of the request, the limit of requests for the event is exhausted,
     * then all unconfirmed requests must be rejected
     *
     * @param userId
     * @param id
     * @param requestsDto
     * @return lists of confirmed and rejected requests
     */
    EventRequestStatusUpdateResult updateRequestsStatus(
            Long userId, Long id, EventRequestStatusUpdateRequest requestsDto);

    /**
     * Returns a list of requests to participate in the event
     * If the initiator is not found throws NotFoundException
     * If the event is not found throws NotFoundException
     *
     * @param userId
     * @param id
     * @return list of user events
     */
    List<ParticipationRequestDto> getRequests(Long userId, Long id);
}
