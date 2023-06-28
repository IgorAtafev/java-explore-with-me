package ru.yandex.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ru.yandex.practicum.ewm.dto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventRequestDto;
import ru.yandex.practicum.ewm.dto.EventShortDto;
import ru.yandex.practicum.ewm.service.EventService;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(
            @PathVariable Long userId,
            @RequestBody @Validated(ValidationOnCreate.class) EventRequestDto eventRequestDto
    ) {
        log.info("Request received POST /users/{}/events: '{}'", userId, eventRequestDto);
        return eventService.createEvent(userId, eventRequestDto);
    }

    @PatchMapping("/{id}")
    public EventFullDto updateEventById(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestBody @Validated(ValidationOnUpdate.class) EventRequestDto eventRequestDto
    ) {
        log.info("Request received PATCH /users/{}/events/{}: '{}'", userId, id, eventRequestDto);
        return eventService.updateUserEvent(userId, id, eventRequestDto);
    }

    @GetMapping
    public List<EventShortDto> getEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        Pageable page = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());

        return eventService.getUserEvents(userId, page);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long userId, @PathVariable Long id) {
        return eventService.getUserEventById(userId, id);
    }
}
