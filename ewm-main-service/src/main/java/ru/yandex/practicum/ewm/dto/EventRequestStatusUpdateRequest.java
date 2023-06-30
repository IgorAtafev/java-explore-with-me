package ru.yandex.practicum.ewm.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.ewm.model.ParticipationRequestStatus;
import ru.yandex.practicum.ewm.validator.ValidationOnUpdate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EventRequestStatusUpdateRequest {

    @NotEmpty(groups = ValidationOnUpdate.class, message = "RequestIds cannot be empty")
    private List<Long> requestIds;

    @NotNull(groups = ValidationOnUpdate.class, message = "Status cannot be null")
    private ParticipationRequestStatus status;
}
