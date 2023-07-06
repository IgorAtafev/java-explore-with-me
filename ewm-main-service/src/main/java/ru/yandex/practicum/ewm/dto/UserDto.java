package ru.yandex.practicum.ewm.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.ewm.validator.ValidationOnCreate;
import ru.yandex.practicum.ewm.validator.ValidationOnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserDto {

    @Null(groups = {ValidationOnCreate.class, ValidationOnUpdate.class}, message = "Id must be null")
    private Long id;

    @NotEmpty(groups = ValidationOnCreate.class, message = "Email cannot be empty")
    @Email(groups = {ValidationOnCreate.class, ValidationOnUpdate.class}, message = "Email must be valid")
    @Size(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            min = 6, max = 254, message = "Email must contain at least 6 and no more than 254 characters"
    )
    private String email;

    @NotBlank(
            groups = ValidationOnCreate.class,
            message = "Name cannot be empty and must contain at least one non-whitespace character"
    )
    @Size(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            min = 2, max = 250, message = "Name must contain at least 2 and no more than 250 characters"
    )
    private String name;
}
