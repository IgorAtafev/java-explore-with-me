package ru.yandex.practicum.ewm.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.ewm.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StatsMapperTest {

    private final StatsMapper statsMapper = new StatsMapper();

    @Test
    void toDto_shouldReturnEndpointHitDto() {
        EndpointHit endpointHit = initEndpointHit();

        EndpointHitDto endpointHitDto = statsMapper.toDto(endpointHit);

        assertThat(endpointHitDto.getId()).isEqualTo(1L);
        assertThat(endpointHitDto.getApp()).isEqualTo("ewm-main-service");
        assertThat(endpointHitDto.getUri()).isEqualTo("/events/1");
        assertThat(endpointHitDto.getIp()).isEqualTo("192.163.0.1");
        assertThat(endpointHitDto.getTimestamp()).isEqualTo(
                LocalDateTime.of(2023, 6, 18, 21, 15, 10));
    }

    @Test
    void toEndpointHit_shouldReturnEndpointHit() {
        EndpointHitDto endpointHitDto = initEndpointHitDto();

        EndpointHit endpointHit = statsMapper.toEndpointHit(endpointHitDto);

        assertThat(endpointHit.getApp()).isEqualTo("ewm-main-service");
        assertThat(endpointHit.getUri()).isEqualTo("/events/1");
        assertThat(endpointHit.getIp()).isEqualTo("192.163.0.1");
        assertThat(endpointHit.getTimestamp()).isEqualTo(
                LocalDateTime.of(2023, 6, 18, 21, 15, 10));
    }

    private EndpointHitDto initEndpointHitDto() {
        EndpointHitDto endpointHitDto = new EndpointHitDto();

        endpointHitDto.setId(1L);
        endpointHitDto.setApp("ewm-main-service");
        endpointHitDto.setUri("/events/1");
        endpointHitDto.setIp("192.163.0.1");
        endpointHitDto.setTimestamp(LocalDateTime.of(2023, 6, 18, 21, 15, 10));

        return endpointHitDto;
    }

    private EndpointHit initEndpointHit() {
        EndpointHit endpointHit = new EndpointHit();

        endpointHit.setId(1L);
        endpointHit.setApp("ewm-main-service");
        endpointHit.setUri("/events/1");
        endpointHit.setIp("192.163.0.1");
        endpointHit.setTimestamp(LocalDateTime.of(2023, 6, 18, 21, 15, 10));

        return endpointHit;
    }
}
