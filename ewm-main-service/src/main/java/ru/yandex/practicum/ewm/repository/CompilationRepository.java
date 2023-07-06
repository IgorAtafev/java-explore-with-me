package ru.yandex.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewm.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    /**
     * Returns a list of compilations by pinned
     *
     * @param pinned
     * @param page
     * @return list of compilations
     */
    List<Compilation> findByPinned(Boolean pinned, Pageable page);
}
