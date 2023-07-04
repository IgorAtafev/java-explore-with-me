package ru.yandex.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.ewm.dto.EventForRequestDto;
import ru.yandex.practicum.ewm.dto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventShortDto;
import ru.yandex.practicum.ewm.model.Event;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {

    public EventFullDto toFullDto(Event event) {
        EventFullDto eventDto = new EventFullDto();

        eventDto.setShortDto(toShortDto(event));
        eventDto.setDescription(event.getDescription());
        eventDto.setLocation(event.getLocation());
        eventDto.setParticipantLimit(event.getParticipantLimit());
        eventDto.setRequestModeration(event.getRequestModeration());
        eventDto.setPublishedOn(event.getPublishedOn());
        eventDto.setState(event.getState());
        eventDto.setCreatedOn(event.getCreated());

        return eventDto;
    }

    public List<EventFullDto> toFullDtos(Collection<Event> events) {
        return events.stream()
                .map(EventMapper::toFullDto)
                .collect(Collectors.toList());
    }

    public EventShortDto toShortDto(Event event) {
        EventShortDto eventDto = new EventShortDto();

        eventDto.setId(event.getId());
        eventDto.setTitle(event.getTitle());
        eventDto.setAnnotation(event.getAnnotation());
        eventDto.setEventDate(event.getEventDate());
        eventDto.setCategory(CategoryMapper.toDto(event.getCategory()));
        eventDto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        eventDto.setPaid(event.getPaid());
        eventDto.setConfirmedRequests(event.getConfirmedRequests());
        eventDto.setViews(event.getViews());

        return eventDto;
    }

    public List<EventShortDto> toShortDtos(Collection<Event> events) {
        return events.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    public Event toEvent(EventForRequestDto eventDto) {
        Event event = new Event();

        event.setId(eventDto.getId());
        event.setTitle(eventDto.getTitle());
        event.setAnnotation(eventDto.getAnnotation());
        event.setDescription(eventDto.getDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setLocation(eventDto.getLocation());
        event.setPaid(eventDto.getPaid());
        event.setParticipantLimit(eventDto.getParticipantLimit());
        event.setRequestModeration(eventDto.getRequestModeration());

        return event;
    }
}
