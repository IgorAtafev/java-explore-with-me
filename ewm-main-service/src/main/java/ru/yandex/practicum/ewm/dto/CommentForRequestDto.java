package ru.yandex.practicum.ewm.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.ewm.validator.ValidationOnCreate;
import ru.yandex.practicum.ewm.validator.ValidationOnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CommentForRequestDto {

    @Null(groups = ValidationOnUpdate.class, message = "Event must be null")
    @NotNull(groups = ValidationOnCreate.class, message = "Event cannot be null")
    @Positive(groups = ValidationOnCreate.class, message = "Event must be a strictly positive number")
    private Long eventId;

    @NotBlank(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            message = "Text cannot be empty and must contain at least one non-whitespace character"
    )
    @Size(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            min = 20, max = 2000, message = "Text must contain at least 20 and no more than 2000 characters"
    )
    private String text;
}
