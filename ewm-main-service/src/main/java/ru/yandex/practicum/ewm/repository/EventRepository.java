package ru.yandex.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.EventState;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    /**
     * Checks for the existence of an event by category id
     *
     * @param categoryId
     * @return true or false
     */
    boolean existsByCategoryId(Long categoryId);

    /**
     * Checks for the existence of an event by id and initiator id
     *
     * @param id
     * @param initiatorId
     * @return event
     */
    boolean existsByIdAndInitiatorId(Long id, Long initiatorId);

    /**
     * Returns event by id and initiator id
     *
     * @param id
     * @param initiatorId
     * @return event
     */
    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);

    /**
     * Returns a list of events by ids
     *
     * @param ids
     * @return list of events
     */
    List<Event> findByIdIn(List<Long> ids);

    /**
     * Returns a list of events by initiator id
     *
     * @param initiatorId
     * @param page
     * @return list of events
     */
    List<Event> findByInitiatorId(Long initiatorId, Pageable page);

    /**
     * Returns event by id and state
     *
     * @param id
     * @param state
     * @return event
     */
    Optional<Event> findByIdAndState(Long id, EventState state);
}
