package ru.yandex.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.dto.CommentShortDto;
import ru.yandex.practicum.ewm.dto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventShortDto;
import ru.yandex.practicum.ewm.service.CommentService;
import ru.yandex.practicum.ewm.service.EventService;
import ru.yandex.practicum.ewm.util.EventRequestParam;
import ru.yandex.practicum.ewm.util.Pagination;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.yandex.practicum.ewm.util.Constants.DATE_TIME_FORMAT;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPublicController {

    private final EventService eventService;
    private final CommentService commentService;

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
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest request
    ) {
        EventRequestParam requestParam = EventRequestParam.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .onlyAvailable(onlyAvailable)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .sort(sort)
                .build();

        Pageable page = new Pagination(from, size, "eventDate");

        log.info("Request received GET /events?text={}&categories={}&paid={}&onlyAvailable={}&rangeStart={}" +
                "&rangeEnd={}&sort={}&from={}&size={}", text, categories, paid, onlyAvailable, rangeStart,
                rangeEnd, sort, from, size);
        return eventService.getPublicEvents(requestParam, request, page);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Request received GET /events/{}", eventId);
        return eventService.getPublicEventById(eventId, request);
    }

    @GetMapping("/{eventId}/comments")
    public List<CommentShortDto> getCommentsToEvent(@PathVariable Long eventId) {
        log.info("Request received GET /events/{}/comments", eventId);
        return commentService.getCommentsToEvent(eventId);
    }

    @GetMapping("/{eventId}/comments/{id}")
    public CommentShortDto getCommentToEventById(@PathVariable Long eventId, @PathVariable Long id) {
        log.info("Request received GET /events/{}/comments/{}", eventId, id);
        return commentService.getCommentToEventById(eventId, id);
    }
}
