package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.CategoryDto;
import ru.yandex.practicum.ewm.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.repository.CategoryRepository;
import ru.yandex.practicum.ewm.validator.ConflictException;
import ru.yandex.practicum.ewm.validator.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toCategory(categoryDto);

        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new ConflictException(String.format("Category with name %s exists", category.getName()));
        }

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public CategoryDto updateCategoryById(Long id, CategoryDto categoryDto) {
        Category category = categoryMapper.toCategory(categoryDto);

        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Category with id %d does not exist", id));
        }

        if (categoryRepository.existsByIdNotAndNameIgnoreCase(id, category.getName())) {
            throw new ConflictException(String.format("Category with name %s exists", category.getName()));
        }

        category.setId(id);

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public void removeCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Category with id %d does not exist", id));
        }

        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDto> getCategories(Pageable page) {
        return categoryMapper.toDtos(categoryRepository.findAll(page).toList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Category with id %d does not exist", id)));

        return categoryMapper.toDto(category);
    }
}
