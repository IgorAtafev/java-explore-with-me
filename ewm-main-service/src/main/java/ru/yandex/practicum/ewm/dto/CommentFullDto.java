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
public class CommentFullDto {

    private Long id;

    private String text;

    private EventShortDto event;

    private UserShortDto author;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime created;
}
