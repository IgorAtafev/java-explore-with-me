package ru.yandex.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.ewm.dto.CategoryDto;
import ru.yandex.practicum.ewm.model.Category;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CategoryMapper {

    public CategoryDto toDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());

        return categoryDto;
    }

    public List<CategoryDto> toDtos(Collection<Category> categories) {
        return categories.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();

        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());

        return category;
    }
}
