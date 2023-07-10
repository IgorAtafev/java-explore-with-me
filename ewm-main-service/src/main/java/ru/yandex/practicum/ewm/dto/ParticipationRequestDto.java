package ru.yandex.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.ewm.model.ParticipationRequestStatus;
import ru.yandex.practicum.ewm.util.DateTimeUtils;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ParticipationRequestDto {

    private Long id;

    private Long event;

    private Long requester;

    private ParticipationRequestStatus status;

    @JsonFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT)
    private LocalDateTime created;
}
