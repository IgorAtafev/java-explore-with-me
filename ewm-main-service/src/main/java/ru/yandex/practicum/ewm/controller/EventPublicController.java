package ru.yandex.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.dto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventShortDto;
import ru.yandex.practicum.ewm.model.EventSortType;
import ru.yandex.practicum.ewm.service.EventService;
import ru.yandex.practicum.ewm.validator.ValidationException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPublicController {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "EVENT_DATE") String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        EventSortType sortType = getSortType(sort);
        Pageable page = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());

        List<EventShortDto> events = eventService.getPublicEvents(text, categories, paid, onlyAvailable,
                rangeStart, rangeEnd, page);
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id) {
        return eventService.getPublicEventById(id);
    }

    private EventSortType getSortType(String sort) {
        try {
            return EventSortType.valueOf(sort.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown sort type: UNSUPPORTED_STATUS");
        }
    }
}
