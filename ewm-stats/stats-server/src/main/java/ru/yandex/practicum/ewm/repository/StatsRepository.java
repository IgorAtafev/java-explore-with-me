package ru.yandex.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewm.model.EndpointHit;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
}
