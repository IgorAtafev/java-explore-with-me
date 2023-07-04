package ru.yandex.practicum.ewm.util;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EventRequestParam {

    private List<Long> users;

    private List<String> states;

    private String text;

    private List<Long> categories;

    private Boolean paid;

    private Boolean onlyAvailable;

    LocalDateTime rangeStart;

    LocalDateTime rangeEnd;

    String sort;
}
