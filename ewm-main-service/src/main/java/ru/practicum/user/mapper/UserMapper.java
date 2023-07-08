package ru.practicum.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.UserModel;

@UtilityClass
public class UserMapper {
    public UserModel toUserModel(NewUserRequest newUserRequest) {
        return new UserModel(
                0,
                newUserRequest.getName(),
                newUserRequest.getEmail()
        );
    }

    public UserDto toUserDto(UserModel userModel) {
        return new UserDto(
                userModel.getId(),
                userModel.getEmail(),
                userModel.getName()
        );
    }
}
