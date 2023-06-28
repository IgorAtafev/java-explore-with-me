package ru.yandex.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.ewm.model.Location;
import ru.yandex.practicum.ewm.model.StateAction;
import ru.yandex.practicum.ewm.validator.ValidationOnCreate;
import ru.yandex.practicum.ewm.validator.ValidationOnUpdate;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EventRequestDto {

    @Null(groups = ValidationOnCreate.class, message = "Id must be null")
    private Long id;

    @NotBlank(
            groups = ValidationOnCreate.class,
            message = "Title cannot be empty and must contain at least one non-whitespace character"
    )
    @Size(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            min = 3, max = 120, message = "Title must contain at least 3 and no more than 120 characters"
    )
    private String title;

    @NotBlank(
            groups = ValidationOnCreate.class,
            message = "Annotation cannot be empty and must contain at least one non-whitespace character"
    )
    @Size(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            min = 20, max = 2000, message = "Annotation must contain at least 20 and no more than 2000 characters"
    )
    private String annotation;

    @NotBlank(
            groups = ValidationOnCreate.class,
            message = "Description cannot be empty and must contain at least one non-whitespace character"
    )
    @Size(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            min = 20, max = 7000, message = "Description must contain at least 20 and no more than 7000 characters"
    )
    private String description;

    @NotNull(groups = ValidationOnCreate.class, message = "Event date cannot be null")
    @Future(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            message = "Event date must be in the future"
    )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(groups = ValidationOnCreate.class, message = "Location date cannot be null")
    private Location location;

    @JsonProperty("category")
    @NotNull(groups = ValidationOnCreate.class, message = "Category date cannot be null")
    @Positive(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            message = "Category must be a strictly positive number"
    )
    private Long categoryId;

    private Boolean paid;

    @PositiveOrZero(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            message = "Participant limit must be a positive number or 0"
    )
    private Integer participantLimit;

    private Boolean requestModeration;

    @Null(groups = ValidationOnCreate.class, message = "State action must be null")
    private StateAction stateAction;
}
