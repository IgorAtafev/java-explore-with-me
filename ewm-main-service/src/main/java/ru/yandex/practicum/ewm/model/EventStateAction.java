package ru.yandex.practicum.ewm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventStateAction {

    PUBLISH_EVENT(EventState.PUBLISHED),
    REJECT_EVENT(EventState.CANCELED),
    SEND_TO_REVIEW(EventState.PENDING),
    CANCEL_REVIEW(EventState.CANCELED);

    private final EventState state;
}
