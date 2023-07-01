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
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Transactional
    @Override
    public CompilationDto createCompilation(CompilationForRequestDto compilationForRequestDto) {
        if (compilationForRequestDto.getPinned() == null) {
            compilationForRequestDto.setPinned(Boolean.FALSE);
        }

        Compilation compilation = toCompilation(compilationForRequestDto, null);

        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long id, CompilationForRequestDto compilationForRequestDto) {
        compilationForRequestDto.setId(id);

        Compilation oldCompilation = compilationRepository.findById(compilationForRequestDto.getId()).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Compilation with id %d does not exist", compilationForRequestDto.getId())));

        Compilation compilation = toCompilation(compilationForRequestDto, oldCompilation);

        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public void removeCompilation(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException(String.format("Compilation with id %d does not exist", id));
        }

        compilationRepository.deleteById(id);
    }

    @Override
    public CompilationDto getCompilationById(Long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Compilation with id %d does not exist", id)));

        return compilationMapper.toDto(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable page) {
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(page).toList();
        } else {
            compilations = compilationRepository.findByPinned(pinned, page);
        }

        return compilationMapper.toDtos(compilations);
    }

    private Compilation toCompilation(CompilationForRequestDto compilationForRequestDto, Compilation oldCompilation) {
        Compilation compilation = compilationMapper.toCompilation(compilationForRequestDto);

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
