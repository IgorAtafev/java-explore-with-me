package ru.yandex.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.ewm.dto.CompilationDto;
import ru.yandex.practicum.ewm.dto.CompilationForRequestDto;

import java.util.List;

public interface CompilationService {

    /**
     * Creates a new compilation
     *
     * @param compilationForRequestDto
     * @return new compilation
     */
    CompilationDto createCompilation(CompilationForRequestDto compilationForRequestDto);

    /**
     * Updates the compilation
     * If the compilation is not found throws NotFoundException
     *
     * @param id
     * @param compilationForRequestDto
     * @return updated compilation
     */
    CompilationDto updateCompilation(Long id, CompilationForRequestDto compilationForRequestDto);

    /**
     * Removes a compilation
     * If the compilation is not found throws NotFoundException
     *
     * @param id
     */
    void removeCompilation(Long id);

    /**
     * Returns a list of compilations
     * Results should be returned page by page
     *
     * @param pinned
     * @param page
     * @return list of compilations
     */
    List<CompilationDto> getCompilations(Boolean pinned, Pageable page);

    /**
     * Returns a compilation by id
     * If the compilation is not found throws NotFoundException
     *
     * @param id
     * @return compilation
     */
    CompilationDto getCompilationById(Long id);
}
