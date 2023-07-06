package ru.yandex.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.yandex.practicum.ewm.util.Constants.DATE_TIME_FORMAT;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EndpointHitDto {

    private Long id;

    @NotBlank(message = "App cannot be empty and must contain at least one non-whitespace character")
    @Size(min = 2, max = 50, message = "App must contain at least 2 and no more than 50 characters")
    private String app;

    @NotBlank(message = "URI cannot be empty and must contain at least one non-whitespace character")
    @Size(max = 255, message = "URI must contain no more than 255 characters")
    private String uri;

    @NotBlank(message = "IP cannot be empty")
    private String ip;

    @NotNull(message = "Timestamp cannot be null")
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime timestamp;
}
