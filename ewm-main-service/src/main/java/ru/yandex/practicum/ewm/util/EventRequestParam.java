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

    List<Long> users;

    List<String> states;

    String text;

    List<Long> categories;

    Boolean paid;

    Boolean onlyAvailable;

    LocalDateTime rangeStart;

    LocalDateTime rangeEnd;
}
