package ru.yandex.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.ewm.dto.CommentForRequestDto;
import ru.yandex.practicum.ewm.dto.CommentFullDto;
import ru.yandex.practicum.ewm.dto.CommentShortDto;
import ru.yandex.practicum.ewm.util.CommentRequestParam;

import java.util.List;

public interface CommentService {

    /**
     * Creates a new comment
     * If the author is not found throws NotFoundException
     * If the event is not found throws NotFoundException
     * If the user does not have a confirmed request to participate in the event throws ConflictException
     * If the user has previously left a comment about this event throws ConflictException
     *
     * @param userId
     * @param commentForRequestDto
     * @return new comment
     */
    CommentFullDto createComment(Long userId, CommentForRequestDto commentForRequestDto);

    /**
     * Updates a user comment
     * If the author is not found throws NotFoundException
     * If the comment is not found throws NotFoundException
     *
     * @param userId
     * @param id
     * @param commentDto
     * @return updated comment
     */
    CommentFullDto updateUserComment(Long userId, Long id, CommentForRequestDto commentDto);

    /**
     * Removes a user comment
     * If the author is not found throws NotFoundException
     * If the comment is not found throws NotFoundException
     *
     * @param userId
     * @param id
     */
    void removeUserComment(Long userId, Long id);

    /**
     * Removes the comment by the admin
     * If the comment is not found throws NotFoundException
     *
     * @param id
     */
    void removeCommentByAdmin(Long id);

    /**
     * Returns a list of user comments
     * Results should be returned page by page
     * If the author is not found throws NotFoundException
     *
     * @param userId
     * @param page
     * @return list of user comments
     */
    List<CommentFullDto> getUserComments(Long userId, Pageable page);

    /**
     * Returns the user comment by id
     * If the author is not found throws NotFoundException
     * If the comment is not found throws NotFoundException
     *
     * @param userId
     * @param id
     * @return comment
     */
    CommentFullDto getUserCommentById(Long userId, Long id);

    /**
     * Returns a list of comments by the admin
     * Results should be returned page by page
     *
     * @param requestParam
     * @param page
     * @return list of comments
     */
    List<CommentFullDto> getCommentsByAdmin(CommentRequestParam requestParam, Pageable page);

    /**
     * Returns a list of comments for the event
     * If the event is not found throws NotFoundException
     *
     * @param eventId
     * @return list of comments for the event
     */
    List<CommentShortDto> getCommentsToEvent(Long eventId);

    /**
     * Returns a comment to the event by id
     * If the event is not found throws NotFoundException
     * If the comment is not found throws NotFoundException
     *
     * @param eventId
     * @param id
     * @return comment
     */
    CommentShortDto getCommentToEventById(Long eventId, Long id);
}
