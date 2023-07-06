package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.CompilationDto;
import ru.yandex.practicum.ewm.dto.CompilationForRequestDto;
import ru.yandex.practicum.ewm.mapper.CompilationMapper;
import ru.yandex.practicum.ewm.model.Compilation;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.repository.CompilationRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.validator.NotFoundException;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(CompilationForRequestDto compilationForRequestDto) {
        if (compilationForRequestDto.getPinned() == null) {
            compilationForRequestDto.setPinned(Boolean.FALSE);
        }

        Compilation compilation = toCompilation(compilationForRequestDto, null);

        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto updateCompilation(Long id, CompilationForRequestDto compilationForRequestDto) {
        compilationForRequestDto.setId(id);

        Compilation oldCompilation = compilationRepository.findById(compilationForRequestDto.getId()).orElseThrow(
                () -> new NotFoundException(String.format("Compilation with id %d does not exist", id)));

        Compilation compilation = toCompilation(compilationForRequestDto, oldCompilation);

        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public void removeCompilation(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException(String.format("Compilation with id %d does not exist", id));
        }

        compilationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getCompilationById(Long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Compilation with id %d does not exist", id)));

        return CompilationMapper.toDto(compilation);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable page) {
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(page).toList();
        } else {
            compilations = compilationRepository.findByPinned(pinned, page);
        }

        return CompilationMapper.toDtos(compilations);
    }

    private Compilation toCompilation(CompilationForRequestDto compilationForRequestDto, Compilation oldCompilation) {
        Compilation compilation = CompilationMapper.toCompilation(compilationForRequestDto);

        List<Event> events;

        if (compilationForRequestDto.getEvents() != null && !compilationForRequestDto.getEvents().isEmpty()) {
            events = eventRepository.findByIdIn(compilationForRequestDto.getEvents());
        } else {
            events = Collections.emptyList();
        }

        if (compilationForRequestDto.getId() != null) {
            if (compilationForRequestDto.getTitle() == null) {
                compilation.setTitle(oldCompilation.getTitle());
            }

            if (compilationForRequestDto.getPinned() == null) {
                compilation.setPinned(oldCompilation.getPinned());
            }

            if (compilationForRequestDto.getEvents() == null || compilationForRequestDto.getEvents().isEmpty()) {
                events = oldCompilation.getEvents();
            }
        }

        compilation.setEvents(events);

        return compilation;
    }
}
