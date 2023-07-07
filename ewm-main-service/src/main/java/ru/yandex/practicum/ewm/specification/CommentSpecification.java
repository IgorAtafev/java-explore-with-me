package ru.yandex.practicum.ewm.specification;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.yandex.practicum.ewm.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class CommentSpecification {

    public Specification<Comment> findByEventIdIn(List<Long> events) {
        return (root, query, cb) -> cb.in(root.<Long>get("event").get("id")).value(events);
    }

    public Specification<Comment> findByAuthorIdIn(List<Long> users) {
        return (root, query, cb) -> cb.in(root.<Long>get("author").get("id")).value(users);
    }

    public Specification<Comment> findByRangeStartGreaterThanEqual(LocalDateTime rangeStart) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("created"), rangeStart);
    }

    public Specification<Comment> findByRangeEndLessThanEqual(LocalDateTime rangeEnd) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("created"), rangeEnd);
    }
}
