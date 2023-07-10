package ru.yandex.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.dto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventFullForRequestDto;
import ru.yandex.practicum.ewm.service.EventService;
import ru.yandex.practicum.ewm.util.DateTimeUtils;
import ru.yandex.practicum.ewm.util.EventRequestParam;
import ru.yandex.practicum.ewm.util.Pagination;
import ru.yandex.practicum.ewm.validator.ValidationOnUpdate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventAdminController {

    private final EventService eventService;

    @PatchMapping("/{id}")
    public EventFullDto updateEvent(
            @PathVariable Long id,
            @RequestBody @Validated(ValidationOnUpdate.class) EventFullForRequestDto eventFullForRequestDto
    ) {
        log.info("Request received PATCH /admin/events/{}: '{}'", id, eventFullForRequestDto);
        return eventService.updateEventByAdmin(id, eventFullForRequestDto);
    }

    @GetMapping
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        EventRequestParam requestParam = EventRequestParam.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();

        Pageable page = new Pagination(from, size, "eventDate");

        log.info("Request received GET /admin/events?users={}&states={}&categories={}&rangeStart={}&rangeEnd={}" +
                        "&from={}&size={}", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getEventsByAdmin(requestParam, page);
    }
}
