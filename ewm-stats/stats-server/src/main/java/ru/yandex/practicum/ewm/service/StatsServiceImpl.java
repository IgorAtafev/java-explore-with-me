package ru.yandex.practicum.ewm.service;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.dto.ViewStatsDto;
import ru.yandex.practicum.ewm.mapper.StatsMapper;
import ru.yandex.practicum.ewm.repository.StatsRepository;
import ru.yandex.practicum.ewm.validator.ValidationException;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.yandex.practicum.ewm.model.QEndpointHit.endpointHit;

@Service
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final JPAQueryFactory queryFactory;
    private final StatsMapper statsMapper;

    public StatsServiceImpl(StatsRepository statsRepository, EntityManager entityManager, StatsMapper statsMapper) {
        this.statsRepository = statsRepository;
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.statsMapper = statsMapper;
    }

    @Transactional
    @Override
    public EndpointHitDto saveEndpointHit(EndpointHitDto endpointHitDto) {
        return statsMapper.toDto(statsRepository.save(statsMapper.toEndpointHit(endpointHitDto)));
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException(String.format("The start of the range must be before the end of the range"));
        }

        NumberExpression<Long> count = endpointHit.ip.count();
        if (Objects.equals(Boolean.TRUE, unique)) {
            count = endpointHit.ip.countDistinct();
        }

        return queryFactory.select(
                Projections.constructor(
                        ViewStatsDto.class,
                        endpointHit.app,
                        endpointHit.uri,
                        count))
                .from(endpointHit)
                .where(getCondition(start, end, uris))
                .groupBy(endpointHit.app, endpointHit.uri)
                .orderBy(count.desc())
                .fetch();
    }

    private BooleanExpression getCondition(LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(endpointHit.timestamp.after(start).or(endpointHit.timestamp.eq(start)));
        conditions.add(endpointHit.timestamp.before(end).or(endpointHit.timestamp.eq(end)));

        if (uris != null) {
            conditions.add(endpointHit.uri.in(uris));
        }

        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }
}
