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
import ru.yandex.practicum.ewm.model.QEndpointHit;
import ru.yandex.practicum.ewm.repository.StatsRepository;
import ru.yandex.practicum.ewm.util.StatsRequestParam;
import ru.yandex.practicum.ewm.validator.ValidationException;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final JPAQueryFactory queryFactory;

    public StatsServiceImpl(StatsRepository statsRepository, EntityManager entityManager) {
        this.statsRepository = statsRepository;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public EndpointHitDto saveEndpointHit(EndpointHitDto endpointHitDto) {
        return StatsMapper.toDto(statsRepository.save(StatsMapper.toEndpointHit(endpointHitDto)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatsDto> getStats(StatsRequestParam requestParam) {
        if (requestParam.getStart().isAfter(requestParam.getEnd())) {
            throw new ValidationException(String.format("The start of the range must be before the end of the range"));
        }

        NumberExpression<Long> count = QEndpointHit.endpointHit.ip.count();
        if (Objects.equals(Boolean.TRUE, requestParam.getUnique())) {
            count = QEndpointHit.endpointHit.ip.countDistinct();
        }

        return queryFactory.select(
                Projections.constructor(
                        ViewStatsDto.class,
                        QEndpointHit.endpointHit.app,
                        QEndpointHit.endpointHit.uri,
                        count))
                .from(QEndpointHit.endpointHit)
                .where(getCondition(requestParam.getStart(), requestParam.getEnd(), requestParam.getUris()))
                .groupBy(QEndpointHit.endpointHit.app, QEndpointHit.endpointHit.uri)
                .orderBy(count.desc())
                .fetch();
    }

    private BooleanExpression getCondition(LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QEndpointHit.endpointHit.timestamp.after(start)
                .or(QEndpointHit.endpointHit.timestamp.eq(start)));
        conditions.add(QEndpointHit.endpointHit.timestamp.before(end)
                .or(QEndpointHit.endpointHit.timestamp.eq(end)));

        if (uris != null) {
            conditions.add(QEndpointHit.endpointHit.uri.in(uris));
        }

        return conditions.stream()
                .reduce(BooleanExpression::and)
                .orElse(null);
    }
}
