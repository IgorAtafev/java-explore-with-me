package ru.yandex.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.dto.EventFullForRequestDto;
import ru.yandex.practicum.ewm.dto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.ewm.dto.EventShortDto;
import ru.yandex.practicum.ewm.dto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.service.EventService;
import ru.yandex.practicum.ewm.service.ParticipationRequestService;
import ru.yandex.practicum.ewm.util.Pagination;
import ru.yandex.practicum.ewm.validator.ValidationOnCreate;
import ru.yandex.practicum.ewm.validator.ValidationOnUpdate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPrivateController {

    private final EventService eventService;
    private final ParticipationRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(
            @PathVariable Long userId,
            @RequestBody @Validated(ValidationOnCreate.class) EventFullForRequestDto eventFullForRequestDto
    ) {
        log.info("Request received POST /users/{}/events: '{}'", userId, eventFullForRequestDto);
        return eventService.createEvent(userId, eventFullForRequestDto);
    }

    @PatchMapping("/{id}")
    public EventFullDto updateEvent(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestBody @Validated(ValidationOnUpdate.class) EventFullForRequestDto eventFullForRequestDto
    ) {
        log.info("Request received PATCH /users/{}/events/{}: '{}'", userId, id, eventFullForRequestDto);
        return eventService.updateUserEvent(userId, id, eventFullForRequestDto);
    }

    @GetMapping
    public List<EventShortDto> getEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        Pageable page = new Pagination(from, size, "eventDate");
        log.info("Request received GET /users/{}/events?from={}&size={}", userId, from, size);
        return eventService.getUserEvents(userId, page);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long userId, @PathVariable Long id) {
        log.info("Request received GET /users/{}/events/{}", userId, id);
        return eventService.getUserEventById(userId, id);
    }

    @PatchMapping("/{id}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatus(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestBody @Validated(ValidationOnUpdate.class) EventRequestStatusUpdateRequest requestsDto
    ) {
        log.info("Request received PATCH /users/{}/events/{}/requests: '{}'", userId, id, requestsDto);
        return requestService.updateRequestsStatus(userId, id, requestsDto);
    }

    @GetMapping("/{id}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId, @PathVariable Long id) {
        log.info("Request received GET /users/{}/events/{}/requests", userId, id);
        return requestService.getRequests(userId, id);
    }
}
