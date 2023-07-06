package ru.yandex.practicum.ewm.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CompilationDto {

    private Long id;

    private String title;

    private Boolean pinned;

    private List<EventShortDto> events;
}
