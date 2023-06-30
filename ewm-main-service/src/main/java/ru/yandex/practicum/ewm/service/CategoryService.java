package ru.yandex.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.ewm.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    /**
     * Creates a new category
     * If a category with the same name exists throws ConflictException
     *
     * @param categoryDto
     * @return new category
     */
    CategoryDto createCategory(CategoryDto categoryDto);

    /**
     * Updates the category
     * If the category is not found throws NotFoundException
     * If a category with the same name exists throws ConflictException
     *
     * @param id
     * @param categoryDto
     * @return updated category
     */
    CategoryDto updateCategoryById(Long id, CategoryDto categoryDto);

    /**
     * Removes a category
     * If the category is not found throws NotFoundException
     * If there are events associated with the category throws ConflictException
     *
     * @param id
     */
    void removeCategoryById(Long id);

    /**
     * Returns a list of categories
     * Results should be returned page by page
     *
     * @param page
     * @return list of categories
     */
    List<CategoryDto> getCategories(Pageable page);

    /**
     * Returns category by id
     * If the category is not found throws NotFoundException
     *
     * @param id
     * @return category
     */
    CategoryDto getCategoryById(Long id);
}
