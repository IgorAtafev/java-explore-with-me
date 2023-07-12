package ru.yandex.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewm.model.ParticipationRequest;
import ru.yandex.practicum.ewm.model.ParticipationRequestStatus;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    /**
     * Checks for the existence of a participation request by requester id and event id
     *
     * @param requesterId
     * @param eventId
     * @return true or false
     */
    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    /**
     * Checks for the existence of a participation request by requester id and event id and status
     *
     * @param requesterId
     * @param eventId
     * @param status
     * @return true or false
     */
    boolean existsByRequesterIdAndEventIdAndStatus(Long requesterId, Long eventId, ParticipationRequestStatus status);

    /**
     * Returns participation request by id and requester id
     *
     * @param id
     * @param requesterId
     * @return participation request
     */
    Optional<ParticipationRequest> findByIdAndRequesterId(Long id, Long requesterId);

    /**
     * Returns a list of participation requests by requester id
     *
     * @param requesterId
     * @return list of participation requests
     */
    List<ParticipationRequest> findByRequesterId(Long requesterId);

    /**
     * Returns the count of participation requests by event id and status
     *
     * @param eventId
     * @param status
     * @return count of participation requests
     */
    long countByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

    /**
     * Returns a list of participation requests by event ids and status
     *
     * @param eventIds
     * @param status
     * @return list of participation requests
     */
    List<ParticipationRequest> findByEventIdInAndStatus(List<Long> eventIds, ParticipationRequestStatus status);

    /**
     * Returns a list of participation requests by ids and event id and status
     *
     * @param ids
     * @param eventId
     * @param status
     * @return list of participation requests
     */
    List<ParticipationRequest> findByIdInAndEventIdAndStatus(
            List<Long> ids, Long eventId, ParticipationRequestStatus status);

    /**
     * Returns a list of participation requests by event id
     *
     * @param eventId
     * @return list of participation requests
     */
    List<ParticipationRequest> findByEventId(Long eventId);
}
