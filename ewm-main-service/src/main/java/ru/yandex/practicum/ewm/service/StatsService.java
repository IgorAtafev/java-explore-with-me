package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.ViewStatsDto;
import ru.yandex.practicum.ewm.util.StatsRequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface StatsService {

    /**
     * Creates a new endpoint hit
     *
     * @param request
     */
    void saveEndpointHit(HttpServletRequest request);

    /**
     * Returns statistics on endpoints
     *
     * @param requestParam
     * @return list of endpoints hits
     */
    List<ViewStatsDto> getStats(StatsRequestParam requestParam);
}
