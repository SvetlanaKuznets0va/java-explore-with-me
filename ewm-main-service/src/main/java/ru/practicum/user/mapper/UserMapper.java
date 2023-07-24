package ru.practicum.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

@UtilityClass
public class UserMapper {
    public User toUserModel(NewUserRequest newUserRequest) {
        return new User(
                0,
                newUserRequest.getName(),
                newUserRequest.getEmail()
        );
    }

    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }

    public UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}
