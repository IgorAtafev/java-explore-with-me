package ru.yandex.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.ewm.util.DateTimeUtils;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EventShortDto {

    private Long id;

    private String title;

    private String annotation;

    @JsonFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    private CategoryDto category;

    private UserShortDto initiator;

    private Boolean paid;

    private Integer confirmedRequests;

    private Integer comments;

    private Long views;
}
