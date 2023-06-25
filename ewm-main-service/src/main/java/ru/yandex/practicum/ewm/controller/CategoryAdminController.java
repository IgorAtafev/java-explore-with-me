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
import ru.yandex.practicum.ewm.dto.CategoryDto;
import ru.yandex.practicum.ewm.service.CategoryService;
import ru.yandex.practicum.shareit.validator.ValidationOnCreate;
import ru.yandex.practicum.shareit.validator.ValidationOnUpdate;

@RestController
@RequestMapping("/admin/categories")
@Slf4j
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Validated(ValidationOnCreate.class) CategoryDto categoryDto) {
        log.info("Request received POST /admin/categories: '{}'", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping("/{id}")
    public CategoryDto updateCategoryById(
            @PathVariable Long id,
            @RequestBody @Validated(ValidationOnUpdate.class) CategoryDto categoryDto
    ) {
        log.info("Request received PATCH /admin/categories/{}: '{}'", id, categoryDto);
        return categoryService.updateCategoryById(id, categoryDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategoryById(@PathVariable Long id) {
        log.info("Request received DELETE /admin/categories/{}", id);
        categoryService.removeCategoryById(id);
    }
}
