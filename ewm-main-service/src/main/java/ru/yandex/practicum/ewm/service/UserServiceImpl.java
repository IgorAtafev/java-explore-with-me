package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.UserDto;
import ru.yandex.practicum.ewm.mapper.UserMapper;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.UserRepository;
import ru.yandex.practicum.ewm.validator.ConflictException;
import ru.yandex.practicum.ewm.validator.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);

        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new ConflictException(String.format("User with email %s exists", user.getEmail()));
        }

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void removeUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("User with id %d does not exist", id));
        }

        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable page) {
        List<User> users;

        if (ids == null) {
            users = userRepository.findAll(page).toList();
        } else {
            users = userRepository.findByIdIn(ids, page);
        }

        return userMapper.toDtos(users);
    }
}
