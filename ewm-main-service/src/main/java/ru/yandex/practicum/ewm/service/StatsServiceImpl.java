package ru.yandex.practicum.ewm.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.client.StatsClient;
import ru.yandex.practicum.ewm.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.dto.ViewStatsDto;
import ru.yandex.practicum.ewm.util.StatsRequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsClient statsClient;

    @Value("${app.name}")
    private String app;

    @Override
    public void saveEndpointHit(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();

        endpointHitDto.setApp(app);
        endpointHitDto.setUri(request.getRequestURI());
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setTimestamp(LocalDateTime.now());

        statsClient.saveEndpointHit(endpointHitDto);
    }

    @Override
    public List<ViewStatsDto> getStats(StatsRequestParam requestParam) {
        ResponseEntity<Object> responseEntity = statsClient.getStats(requestParam);
        Object body = responseEntity.getBody();

        if (body == null) {
            return Collections.emptyList();
        }

        return new ObjectMapper().convertValue(body, new TypeReference<>() {
        });
    }
}
