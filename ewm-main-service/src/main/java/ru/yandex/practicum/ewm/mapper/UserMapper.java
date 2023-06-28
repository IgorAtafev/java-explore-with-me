package ru.yandex.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.dto.UserDto;
import ru.yandex.practicum.ewm.dto.UserShortDto;
import ru.yandex.practicum.ewm.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());

        return userDto;
    }

    public UserShortDto toShortDto(User user) {
        UserShortDto userDto = new UserShortDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());

        return userDto;
    }

    public List<UserDto> toDtos(Collection<User> users) {
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public User toUser(UserDto userDto) {
        User user = new User();

        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());

        return user;
    }
}
