package ru.yandex.practicum.ewm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventSortType {

    EVENT_DATE("eventDate"),
    VIEWS("views");

    private final String type;
}
