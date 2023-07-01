package ru.yandex.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.dto.CompilationDto;
import ru.yandex.practicum.ewm.dto.CompilationForRequestDto;
import ru.yandex.practicum.ewm.service.CompilationService;
import ru.yandex.practicum.ewm.validator.ValidationOnCreate;
import ru.yandex.practicum.ewm.validator.ValidationOnUpdate;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
@RequiredArgsConstructor
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(
            @RequestBody @Validated(ValidationOnCreate.class) CompilationForRequestDto compilationForRequestDto
    ) {
        log.info("Request received POST /admin/compilations: '{}'", compilationForRequestDto);
        return compilationService.createCompilation(compilationForRequestDto);
    }

    @PatchMapping("/{id}")
    public CompilationDto updateCompilation(
            @PathVariable Long id,
            @RequestBody @Validated(ValidationOnUpdate.class) CompilationForRequestDto compilationForRequestDto
    ) {
        log.info("Request received PATCH /admin/compilations/{}: '{}'", id, compilationForRequestDto);
        return compilationService.updateCompilation(id, compilationForRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCompilation(@PathVariable Long id) {
        log.info("Request received DELETE /admin/compilations/{}", id);
        compilationService.removeCompilation(id);
    }
}
