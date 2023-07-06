package ru.yandex.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.ewm.dto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventFullForRequestDto;
import ru.yandex.practicum.ewm.dto.EventShortDto;
import ru.yandex.practicum.ewm.util.EventRequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    /**
     * Creates a new event
     * If the initiator is not found throws NotFoundException
     * If the category is not found throws NotFoundException
     *
     * @param userId
     * @param eventFullForRequestDto
     * @return new event
     */
    EventFullDto createEvent(Long userId, EventFullForRequestDto eventFullForRequestDto);

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
     * @param eventFullForRequestDto
     * @return updated event
     */
    EventFullDto updateUserEvent(Long userId, Long id, EventFullForRequestDto eventFullForRequestDto);

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
     * @param eventFullForRequestDto
     * @return updated event
     */
    EventFullDto updateEventByAdmin(Long id, EventFullForRequestDto eventFullForRequestDto);

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
     * @param requestParam
     * @param page
     * @return list of events
     */
    List<EventFullDto> getEventsByAdmin(EventRequestParam requestParam, Pageable page);

    /**
     * Returns a list of public events
     * Results should be returned page by page
     *
     * @param requestParam
     * @param request
     * @param page
     * @return list of events
     */
    List<EventShortDto> getPublicEvents(EventRequestParam requestParam, HttpServletRequest request, Pageable page);

    /**
     * Returns a public event by id
     * If the event is not found throws NotFoundException
     *
     * @param id
     * @param request
     * @return event
     */
    EventFullDto getPublicEventById(Long id, HttpServletRequest request);
}
