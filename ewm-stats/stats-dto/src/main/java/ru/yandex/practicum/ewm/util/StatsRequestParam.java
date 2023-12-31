package ru.yandex.practicum.ewm.util;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StatsRequestParam {

    private LocalDateTime start;

    private LocalDateTime end;

    private List<String> uris;

    private Boolean unique;
}
