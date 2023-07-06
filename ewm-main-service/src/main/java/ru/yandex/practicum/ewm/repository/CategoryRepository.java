package ru.yandex.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewm.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>  {

    /**
     * Checks for the existence of a category by name
     *
     * @param name
     * @return true or false
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Checks for the existence of a category by name given the specified id
     *
     * @param id
     * @param name
     * @return true or false
     */
    boolean existsByIdNotAndNameIgnoreCase(Long id, String name);
}
