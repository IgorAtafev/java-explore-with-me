package ru.yandex.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.dto.CommentFullDto;
import ru.yandex.practicum.ewm.service.CommentService;
import ru.yandex.practicum.ewm.util.CommentRequestParam;
import ru.yandex.practicum.ewm.util.Pagination;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.yandex.practicum.ewm.util.Constants.DATE_TIME_FORMAT;

@RestController
@RequestMapping("/admin/comments")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CommentAdminController {

    private final CommentService commentService;

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeComment(@PathVariable Long id) {
        log.info("Request received DELETE /admin/comments/{}", id);
        commentService.removeCommentByAdmin(id);
    }

    @GetMapping
    public List<CommentFullDto> getComments(
            @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        CommentRequestParam requestParam = CommentRequestParam.builder()
                .events(events)
                .users(users)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();

        Pageable page = new Pagination(from, size, "created");

        log.info("Request received GET /admin/comments?events={}&users={}&rangeStart={}&rangeEnd={}" +
                "&from={}&size={}", events, users, rangeStart, rangeEnd, from, size);
        return commentService.getCommentsByAdmin(requestParam, page);
    }
}
