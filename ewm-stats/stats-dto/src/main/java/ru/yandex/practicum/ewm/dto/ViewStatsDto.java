package ru.yandex.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class ViewStatsDto {

    private String app;

    private String uri;

    private Long hits;
}
