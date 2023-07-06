package ru.yandex.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.ewm.model.ParticipationRequestStatus;

import java.time.LocalDateTime;

import static ru.yandex.practicum.ewm.util.Constants.DATE_TIME_FORMAT;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ParticipationRequestDto {

    private Long id;

    private Long event;

    private Long requester;

    private ParticipationRequestStatus status;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime created;
}
