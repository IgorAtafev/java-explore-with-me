package ru.yandex.practicum.ewm.model;

public enum StateAction {

    PUBLISH_EVENT(EventState.PUBLISHED),
    REJECT_EVENT(EventState.CANCELED),
    SEND_TO_REVIEW(EventState.PENDING),
    CANCEL_REVIEW(EventState.CANCELED);

    private final EventState state;

    StateAction(EventState state) {
        this.state = state;
    }

    public EventState getState() {
        return state;
    }
}
