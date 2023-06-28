package ru.yandex.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.yandex.practicum.ewm.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    /**
     * Checks for the existence of an event by category id
     *
     * @param categoryId
     * @return true or false
     */
    boolean existsByCategoryId(Long categoryId);

    /**
     * Returns event by id and initiator id
     *
     * @param id
     * @param initiatorId
     * @return event by id and initiator id
     */
    Optional<Event> findByInitiatorIdAndId(Long initiatorId, Long id);

    /**
     * Returns a list of events by initiator
     *
     * @param initiatorId
     * @param page
     * @return list of events
     */
    List<Event> findByInitiatorId(Long initiatorId, Pageable page);
}
