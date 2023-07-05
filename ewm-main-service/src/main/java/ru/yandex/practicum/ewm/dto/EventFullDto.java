package ru.yandex.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.ewm.model.EventState;
import ru.yandex.practicum.ewm.model.Location;

import java.time.LocalDateTime;

import static ru.yandex.practicum.ewm.util.Constants.DATE_TIME_FORMAT;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EventFullDto {

    @JsonUnwrapped
    private EventShortDto shortDto = new EventShortDto();

    private String description;

    private Location location;

    private Integer participantLimit;

    private Boolean requestModeration;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime publishedOn;

    private EventState state;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime createdOn;
}
