package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {

    /**
     * Creates a new participation request
     * If the requester is not found throws NotFoundException
     * If the event is not found throws NotFoundException
     * If the user has previously created an event request throws ConflictException
     * If the user is the initiator of the event throws ConflictException
     * If the event is published throws ConflictException
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
}
