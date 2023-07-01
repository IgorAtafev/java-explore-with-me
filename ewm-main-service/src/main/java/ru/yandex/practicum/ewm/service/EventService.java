package ru.yandex.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.ewm.dto.EventForRequestDto;
import ru.yandex.practicum.ewm.dto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.ewm.dto.EventShortDto;
import ru.yandex.practicum.ewm.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    /**
     * Creates a new event
     * If the initiator is not found throws NotFoundException
     * If the category is not found throws NotFoundException
     *
     * @param userId
     * @param eventForRequestDto
     * @return new event
     */
    EventFullDto createEvent(Long userId, EventForRequestDto eventForRequestDto);

    /**
     * Updates a user event
     * If the initiator is not found throws NotFoundException
     * If the category is not found throws NotFoundException
     * If the event is not found throws NotFoundException
     * If the date of the event is earlier than two hours from the current moment throws ConflictException
     * If the action is unknown state throws ConflictException
     * If the event is published throws ConflictException
     *
     * @param userId
     * @param id
     * @param eventForRequestDto
     * @return updated event
     */
    EventFullDto updateUserEvent(Long userId, Long id, EventForRequestDto eventForRequestDto);

    /**
     * Updates the event by the admin
     * If the initiator is not found throws NotFoundException
     * If the category is not found throws NotFoundException
     * If the event is not found throws NotFoundException
     * If the date of the event is earlier than two hours from the current moment throws ConflictException
     * If the action is unknown state throws ConflictException
     * If the date of the event is earlier than one hour from the publishing date throws ConflictException
     * If the event has been published and is not in the publish pending state throws ConflictException
     * If the event is canceled and published throws ConflictException
     *
     * @param id
     * @param eventForRequestDto
     * @return updated event
     */
    EventFullDto updateEventByAdmin(Long id, EventForRequestDto eventForRequestDto);

    /**
     * Returns a list of user events
     * Results should be returned page by page
     * If the initiator is not found throws NotFoundException
     *
     * @param userId
     * @param page
     * @return list of user events
     */
    List<EventShortDto> getUserEvents(Long userId, Pageable page);

    /**
     * Returns the user event by id
     * If the initiator is not found throws NotFoundException
     * If the event is not found throws NotFoundException
     *
     * @param userId
     * @param id
     * @return event
     */
    EventFullDto getUserEventById(Long userId, Long id);

    /**
     * Returns a list of events by the admin
     * Results should be returned page by page
     *
     * @param users
     * @param states
     * @param categories
     * @param rangeStart
     * @param rangeEnd
     * @param page
     * @return list of events
     */
    List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);

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

    /**
     * Returns a list of public events
     * Results should be returned page by page
     *
     * @param text
     * @param categories
     * @param paid
     * @param onlyAvailable
     * @param rangeStart
     * @param rangeEnd
     * @param page
     * @return list of events
     */
    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid, Boolean onlyAvailable,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);

    /**
     * Returns a public event by id
     * If the event is not found throws NotFoundException
     *
     * @param id
     * @return event
     */
    EventFullDto getPublicEventById(Long id);
}