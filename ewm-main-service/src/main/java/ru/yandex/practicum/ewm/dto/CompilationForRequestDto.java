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
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CompilationForRequestDto {

    @Null(groups = {ValidationOnCreate.class, ValidationOnUpdate.class}, message = "Id must be null")
    private Long id;

    @NotBlank(
            groups = ValidationOnCreate.class,
            message = "Title cannot be empty and must contain at least one non-whitespace character"
    )
    @Size(
            groups = {ValidationOnCreate.class, ValidationOnUpdate.class},
            max = 50, message = "Title must contain no more than 50 characters"
    )
    private String title;

    private Boolean pinned;

    private List<Long> events;
}
