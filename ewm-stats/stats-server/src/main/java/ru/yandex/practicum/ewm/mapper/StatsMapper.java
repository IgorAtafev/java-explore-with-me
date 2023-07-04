package ru.yandex.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.ewm.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.model.EndpointHit;

@UtilityClass
public class StatsMapper {

    public EndpointHitDto toDto(EndpointHit endpointHit) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();

        endpointHitDto.setId(endpointHit.getId());
        endpointHitDto.setApp(endpointHit.getApp());
        endpointHitDto.setUri(endpointHit.getUri());
        endpointHitDto.setIp(endpointHit.getIp());
        endpointHitDto.setTimestamp(endpointHit.getTimestamp());

        return endpointHitDto;
    }

    public EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();

        endpointHit.setApp(endpointHitDto.getApp());
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setIp(endpointHitDto.getIp());
        endpointHit.setTimestamp(endpointHitDto.getTimestamp());

        return endpointHit;
    }
}
