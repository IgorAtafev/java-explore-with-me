package ru.yandex.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.yandex.practicum.ewm.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    /**
     * Checks for the existence of a comment by author id and event id
     *
     * @param authorId
     * @param eventId
     * @return true or false
     */
    boolean existsByAuthorIdAndEventId(Long authorId, Long eventId);

    /**
     * Checks for the existence of a comment by id and author id
     *
     * @param id
     * @param authorId
     * @return true or false
     */
    boolean existsByIdAndAuthorId(Long id, Long authorId);

    /**
     * Returns comment by id and author id
     *
     * @param id
     * @param authorId
     * @return comment
     */
    Optional<Comment> findByIdAndAuthorId(Long id, Long authorId);

    /**
     * Returns a list of comments by author id
     *
     * @param authorId
     * @param page
     * @return list of comments
     */
    List<Comment> findByAuthorId(Long authorId, Pageable page);

    /**
     * Returns comment by id and event id
     *
     * @param id
     * @param eventId
     * @return comment
     */
    Optional<Comment> findByIdAndEventId(Long id, Long eventId);

    /**
     * Returns a list of comments by event id
     *
     * @param eventId
     * @return list of comments
     */
    List<Comment> findByEventId(Long eventId);

    /**
     * Returns a list of comments by event ids
     *
     * @param eventIds
     * @return list of comments
     */
    List<Comment> findByEventIdIn(List<Long> eventIds);

    /**
     * Returns the count of comments by event id
     *
     * @param eventId
     * @return count of comments
     */
    long countByEventId(Long eventId);
}
