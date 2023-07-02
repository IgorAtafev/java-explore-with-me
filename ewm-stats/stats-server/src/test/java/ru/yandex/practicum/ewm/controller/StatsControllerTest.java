package ru.yandex.practicum.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.ewm.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.dto.ViewStatsDto;
import ru.yandex.practicum.ewm.service.StatsService;
import ru.yandex.practicum.ewm.util.StatsRequestParam;
import ru.yandex.practicum.ewm.validator.ErrorHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mockMvc;

    @Mock
    private StatsService statsService;

    @InjectMocks
    private StatsController statsController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(statsController)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void getStats_shouldReturnEmptyStats() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 5, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2035, 5, 1, 0, 0, 0);
        List<String> uris = List.of("/events", "/events/1");
        Boolean unique = false;

        mockMvc.perform(get("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                        start.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                        end.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                        String.join(",", uris),
                        unique))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        StatsRequestParam requestParam = StatsRequestParam.builder()
                .start(start)
                .end(end)
                .uris(uris)
                .unique(unique)
                .build();

        verify(statsService, times(1)).getStats(requestParam);
        verifyNoMoreInteractions(statsService);
    }

    @Test
    void getStats_shouldReturnStats() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 5, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2035, 5, 1, 0, 0, 0);
        List<String> uris = List.of("/events", "/events/1");
        Boolean unique = false;

        ViewStatsDto viewStatsDto1 = initViewStatsDto();
        ViewStatsDto viewStatsDto2 = initViewStatsDto();

        List<ViewStatsDto> expected = List.of(viewStatsDto1, viewStatsDto2);

        String json = objectMapper.writeValueAsString(expected);

        StatsRequestParam requestParam = StatsRequestParam.builder()
                .start(start)
                .end(end)
                .uris(uris)
                .unique(unique)
                .build();

        when(statsService.getStats(requestParam)).thenReturn(expected);

        mockMvc.perform(get("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                        start.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                        end.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                        String.join(",", uris),
                        unique))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(statsService, times(1)).getStats(requestParam);
        verifyNoMoreInteractions(statsService);
    }

    @Test
    void saveEndpointHit_shouldResponseWithOk() throws Exception {
        EndpointHitDto endpointHitDto = initEndpointHitDto();

        String json = objectMapper.writeValueAsString(endpointHitDto);

        when(statsService.saveEndpointHit(endpointHitDto)).thenReturn(endpointHitDto);

        mockMvc.perform(post("/hit").contentType("application/json").content(json))
                .andExpect(status().isCreated());

        verify(statsService, times(1)).saveEndpointHit(endpointHitDto);
        verifyNoMoreInteractions(statsService);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidEndpoints")
    void saveEndpointHit_shouldResponseWithBadRequest_ifTheEndpointIsInvalid(EndpointHitDto endpointHitDto)
            throws Exception {
        String json = objectMapper.writeValueAsString(endpointHitDto);

        mockMvc.perform(post("/hit").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidEndpoints() {
        return Stream.of(
                Arguments.of(initEndpointHitDto(dto -> dto.setApp(null))),
                Arguments.of(initEndpointHitDto(dto -> dto.setApp(""))),
                Arguments.of(initEndpointHitDto(dto -> dto.setApp("   "))),
                Arguments.of(initEndpointHitDto(dto -> dto.setApp("e"))),
                Arguments.of(initEndpointHitDto(dto -> dto.setApp("ewm-m".repeat(10) + "a"))),
                Arguments.of(initEndpointHitDto(dto -> dto.setUri(null))),
                Arguments.of(initEndpointHitDto(dto -> dto.setUri(""))),
                Arguments.of(initEndpointHitDto(dto -> dto.setUri("   "))),
                Arguments.of(initEndpointHitDto(dto -> dto.setUri("/even".repeat(51) + "t"))),
                Arguments.of(initEndpointHitDto(dto -> dto.setIp(null))),
                Arguments.of(initEndpointHitDto(dto -> dto.setIp("   "))),
                Arguments.of(initEndpointHitDto(dto -> dto.setTimestamp(null)))
        );
    }

    private static EndpointHitDto initEndpointHitDto(Consumer<EndpointHitDto> consumer) {
        EndpointHitDto endpointHitDto = initEndpointHitDto();
        consumer.accept(endpointHitDto);
        return endpointHitDto;
    }

    private static EndpointHitDto initEndpointHitDto() {
        EndpointHitDto endpointHitDto = new EndpointHitDto();

        endpointHitDto.setApp("ewm-main-service");
        endpointHitDto.setUri("/events/1");
        endpointHitDto.setIp("192.163.0.1");
        endpointHitDto.setTimestamp(LocalDateTime.of(2023, 6, 18, 21, 15, 10));

        return endpointHitDto;
    }

    private ViewStatsDto initViewStatsDto() {
        return new ViewStatsDto("ewm-main-service", "/events/1", 2L);
    }
}
