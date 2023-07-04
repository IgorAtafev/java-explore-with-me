package ru.yandex.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.ewm.dto.CompilationDto;
import ru.yandex.practicum.ewm.dto.CompilationForRequestDto;
import ru.yandex.practicum.ewm.model.Compilation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public CompilationDto toDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();

        compilationDto.setId(compilation.getId());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setEvents(EventMapper.toShortDtos(compilation.getEvents()));

        return compilationDto;
    }

    public List<CompilationDto> toDtos(Collection<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    public Compilation toCompilation(CompilationForRequestDto compilationDto) {
        Compilation compilation = new Compilation();

        compilation.setId(compilationDto.getId());
        compilation.setTitle(compilationDto.getTitle());
        compilation.setPinned(compilationDto.getPinned());

        return compilation;
    }
}
