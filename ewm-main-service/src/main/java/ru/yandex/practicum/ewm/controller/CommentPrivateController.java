package ru.yandex.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.dto.CommentForRequestDto;
import ru.yandex.practicum.ewm.dto.CommentFullDto;
import ru.yandex.practicum.ewm.service.CommentService;
import ru.yandex.practicum.ewm.util.Pagination;
import ru.yandex.practicum.ewm.validator.ValidationOnCreate;
import ru.yandex.practicum.ewm.validator.ValidationOnUpdate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@Slf4j
@RequiredArgsConstructor
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto createComment(
            @PathVariable Long userId,
            @RequestBody @Validated(ValidationOnCreate.class) CommentForRequestDto commentForRequestDto
    ) {
        log.info("Request received POST /users/{}/comments: '{}'", userId, commentForRequestDto);
        return commentService.createComment(userId, commentForRequestDto);
    }

    @PatchMapping("/{id}")
    public CommentFullDto updateComment(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestBody @Validated(ValidationOnUpdate.class) CommentForRequestDto commentForRequestDto
    ) {
        log.info("Request received PATCH /users/{}/comments/{}: '{}'", userId, id, commentForRequestDto);
        return commentService.updateUserComment(userId, id, commentForRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeComment(@PathVariable Long userId, @PathVariable Long id) {
        log.info("Request received DELETE /users/{}/comments/{}", userId, id);
        commentService.removeUserComment(userId, id);
    }

    @GetMapping
    public List<CommentFullDto> getComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        Pageable page = new Pagination(from, size, "created");
        log.info("Request received GET /users/{}/comments?from={}&size={}", userId, from, size);
        return commentService.getUserComments(userId, page);
    }

    @GetMapping("/{id}")
    public CommentFullDto getCommentById(@PathVariable Long userId, @PathVariable Long id) {
        log.info("Request received GET /users/{}/comments/{}", userId, id);
        return commentService.getUserCommentById(userId, id);
    }
}
