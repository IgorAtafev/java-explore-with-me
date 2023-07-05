package ru.yandex.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.ewm.model.EventStateAction;
import ru.yandex.practicum.ewm.model.Location;
import ru.yandex.practicum.ewm.validator.ValidationOnCreate;
import ru.yandex.practicum.ewm.validator.ValidationOnUpdate;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EventFullForRequestDto {

    @JsonUnwrapped
    @Valid
    private EventShortForRequestDto shortDto = new EventShortForRequestDto();

    @NotBlank(
            groups = ValidationOnCreate.class,
            message = "Description cannot be empty and must contain at least one non-whitespace character"
    )
    @Size(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            min = 20, max = 7000, message = "Description must contain at least 20 and no more than 7000 characters"
    )
    private String description;

    @NotNull(groups = ValidationOnCreate.class, message = "Location date cannot be null")
    private Location location;

    @PositiveOrZero(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            message = "Participant limit must be a positive number or 0"
    )
    private Integer participantLimit;

    private Boolean requestModeration;

    @Null(groups = ValidationOnCreate.class, message = "State action must be null")
    private EventStateAction stateAction;
}
