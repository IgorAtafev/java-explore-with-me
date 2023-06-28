package ru.yandex.practicum.ewm.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Embeddable
public class Location {

    private Double lat;
    private Double lon;
}
