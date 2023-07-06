package ru.yandex.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.dto.CompilationDto;
import ru.yandex.practicum.ewm.service.CompilationService;
import ru.yandex.practicum.ewm.util.Pagination;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        Pageable page = new Pagination(from, size, "id");
        log.info("Request received GET /compilations?from={}&size={}", from, size);
        return compilationService.getCompilations(pinned, page);
    }

    @GetMapping("/{id}")
    public CompilationDto getCompilationById(@PathVariable Long id) {
        log.info("Request received GET /compilations/{}", id);
        return compilationService.getCompilationById(id);
    }
}
