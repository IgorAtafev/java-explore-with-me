package ru.yandex.practicum.ewm.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.EventState;
import ru.yandex.practicum.ewm.model.ParticipationRequest;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.List;

public class EventSpecification {

    public static Specification<Event> findByInitiatorIdIn(List<Long> users) {
        return (root, query, cb) -> cb.in(root.<Long>get("initiator").get("id")).value(users);
    }

    public static Specification<Event> findByStatesIn(List<EventState> eventStates) {
        return (root, query, cb) -> cb.in(root.get("state")).value(eventStates);
    }

    public static Specification<Event> findByCategoryIdIn(List<Long> categories) {
        return (root, query, cb) -> cb.in(root.<Long>get("category").get("id")).value(categories);
    }

    public static Specification<Event> findByTextContaining(String text) {
        return (root, query, cb) -> cb.or(
                cb.like(cb.upper(root.get("annotation")), "%" + text.toUpperCase() + "%"),
                cb.like(cb.upper(root.get("description")), "%" + text.toUpperCase() + "%")
        );
    }

    public static Specification<Event> isPublished() {
        return (root, query, cb) -> cb.equal(root.get("state"), EventState.PUBLISHED);
    }

    public static Specification<Event> findByPaid(Boolean paid) {
        if (Boolean.TRUE.equals(paid)) {
            return (root, query, cb) -> cb.isTrue(root.get("paid"));
        }

        return (root, query, cb) -> cb.isFalse(root.get("paid"));
    }

    public static Specification<Event> findByOnlyAvailable() {
        return (root, query, cb) -> {
            Subquery<Long> subQuery = query.subquery(Long.class);
            Root<ParticipationRequest> subRoot = subQuery.from(ParticipationRequest.class);

            subQuery.select(cb.count(subRoot.get("id")))
                    .where(cb.equal(root.get("id"), subRoot.<Long>get("event").get("id")));

            return cb.and(
                    cb.greaterThan(root.get("participantLimit"), 0),
                    cb.lessThan(root.get("participantLimit"), subQuery)
            );
        };
    }

    public static Specification<Event> findByRangeStartGreaterThanEqual(LocalDateTime rangeStart) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart);
    }

    public static Specification<Event> findByRangeEndLessThanEqual(LocalDateTime rangeEnd) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd);
    }
}
