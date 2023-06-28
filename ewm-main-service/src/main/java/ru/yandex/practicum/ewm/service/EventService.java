package ru.yandex.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.ewm.dto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventRequestDto;
import ru.yandex.practicum.ewm.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    /**
     * Creates a new event
     * If the initiator is not found throws NotFoundException
     * If the category is not found throws NotFoundException
     *
     * @param eventRequestDto
     * @return new event
     */
    EventFullDto createEvent(Long userId, EventRequestDto eventRequestDto);

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
     * @param eventRequestDto
     * @return updated event
     */
    EventFullDto updateUserEvent(Long userId, Long id, EventRequestDto eventRequestDto);

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
     * @param eventRequestDto
     * @return updated event
     */
    EventFullDto updateEventByAdmin(Long id, EventRequestDto eventRequestDto);

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
     * @return event by id
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
}
