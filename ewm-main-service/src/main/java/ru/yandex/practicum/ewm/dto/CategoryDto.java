package ru.yandex.practicum.ewm.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.ewm.validator.ValidationOnCreate;
import ru.yandex.practicum.ewm.validator.ValidationOnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CategoryDto {

    @Null(groups = ValidationOnCreate.class, message = "Id must be null")
    private Long id;

    @NotBlank(
            groups = ValidationOnCreate.class,
            message = "Name cannot be empty and must contain at least one non-whitespace character"
    )
    @Size(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            min = 2, max = 50, message = "App must contain at least 2 and no more than 50 characters"
    )
    private String name;
}
