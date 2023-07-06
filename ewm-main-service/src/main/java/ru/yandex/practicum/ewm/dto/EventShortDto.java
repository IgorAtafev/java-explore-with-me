package ru.yandex.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

import static ru.yandex.practicum.ewm.util.Constants.DATE_TIME_FORMAT;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EventShortDto {

    private Long id;

    private String title;

    private String annotation;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    private CategoryDto category;

    private UserShortDto initiator;

    private Boolean paid;

    private Integer confirmedRequests;

    private Long views;
}
