package ru.yandex.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewm.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks for the existence of a user by email
     *
     * @param email
     * @return true or false
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Returns a list of users by ids
     *
     * @param ids
     * @param page
     * @return list of users
     */
    List<User> findByIdIn(List<Long> ids, Pageable page);
}
