package ru.yandex.practicum.ewm.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.ewm.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.dto.ViewStatsDto;
import ru.yandex.practicum.ewm.mapper.StatsMapper;
import ru.yandex.practicum.ewm.model.EndpointHit;
import ru.yandex.practicum.ewm.repository.StatsRepository;
import ru.yandex.practicum.ewm.validator.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    @Mock
    private StatsRepository statsRepository;

    @Mock
    private StatsMapper statsMapper;

    @InjectMocks
    private StatsServiceImpl statsService;

    @Test
    void getStats_shouldThrowAnException_ifTheStartDateAndTimeIsGreaterThanTheEndDateAndTime() {
        LocalDateTime start = LocalDateTime.of(2023, 5, 1, 0, 2, 0);
        LocalDateTime end = LocalDateTime.of(2023, 5, 1, 0, 0, 0);

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> statsService.getStats(start, end, List.of(), false));

        verifyNoInteractions(statsRepository, statsMapper);
    }

    @Test
    void saveEndpointHit_shouldSaveTheEndpoint() {
        EndpointHitDto endpointHitDto = initEndpointHitDto();
        EndpointHit endpointHit = initEndpointHit();

        when(statsMapper.toEndpointHit(endpointHitDto)).thenReturn(endpointHit);
        when(statsRepository.save(endpointHit)).thenReturn(endpointHit);
        when(statsMapper.toDto(endpointHit)).thenReturn(endpointHitDto);

        assertThat(statsService.saveEndpointHit(endpointHitDto)).isEqualTo(endpointHitDto);

        verify(statsMapper, times(1)).toEndpointHit(endpointHitDto);
        verify(statsRepository, times(1)).save(endpointHit);
        verify(statsMapper, times(1)).toDto(endpointHit);
        verifyNoMoreInteractions(statsMapper, statsRepository);
    }

    private EndpointHitDto initEndpointHitDto() {
        EndpointHitDto endpointHitDto = new EndpointHitDto();

        endpointHitDto.setApp("ewm-main-service");
        endpointHitDto.setUri("/events/1");
        endpointHitDto.setIp("192.163.0.1");
        endpointHitDto.setTimestamp(LocalDateTime.of(2023, 6, 18, 21, 15, 10));

        return endpointHitDto;
    }

    private EndpointHit initEndpointHit() {
        EndpointHit endpointHit = new EndpointHit();

        endpointHit.setApp("ewm-main-service");
        endpointHit.setUri("/events/1");
        endpointHit.setIp("192.163.0.1");
        endpointHit.setTimestamp(LocalDateTime.of(2023, 6, 18, 21, 15, 10));

        return endpointHit;
    }

    private ViewStatsDto initViewStatsDto() {
        return new ViewStatsDto("ewm-main-service", "/events/1", 2L);
    }
}
