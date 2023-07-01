package ru.yandex.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.ewm.dto.UserDto;

import java.util.List;

public interface UserService {

    /**
     * Creates a new user
     * If a user with the same email exists throws ConflictException
     *
     * @param userDto
     * @return new user
     */
    UserDto createUser(UserDto userDto);

    /**
     * Removes a user
     * If the user is not found throws NotFoundException
     *
     * @param id
     */
    void removeUser(Long id);

    /**
     * Returns a list of users
     * Results should be returned page by page
     *
     * @param ids
     * @param page
     * @return list of users
     */
    List<UserDto> getUsers(List<Long> ids, Pageable page);
}
