package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.dto.ViewStatsDto;
import ru.yandex.practicum.ewm.util.StatsRequestParam;

import java.util.List;

public interface StatsService {

    /**
     * Creates a new endpoint hit
     *
     * @param endpointHitDto
     * @return new endpoint hit
     */
    EndpointHitDto saveEndpointHit(EndpointHitDto endpointHitDto);

    /**
     * Returns statistics on endpoints
     * If the start date and time is greater than the end date and time throws NotFoundException
     *
     * @param requestParam
     * @return list of endpoints hits
     */
    List<ViewStatsDto> getStats(StatsRequestParam requestParam);
}
