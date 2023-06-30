package ru.yandex.practicum.ewm.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "participation_requests")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column
    private ParticipationRequestStatus status;

    @Column
    private LocalDateTime created;
}
