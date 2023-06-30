package ru.yandex.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.dto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.model.ParticipationRequest;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ParticipationRequestMapper {

    public ParticipationRequestDto toDto(ParticipationRequest request) {
        ParticipationRequestDto requestDto = new ParticipationRequestDto();

        requestDto.setId(request.getId());
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setRequester(request.getRequester().getId());
        requestDto.setStatus(request.getStatus());
        requestDto.setCreated(request.getCreated());

        return requestDto;
    }

    public List<ParticipationRequestDto> toDtos(Collection<ParticipationRequest> requests) {
        return requests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
