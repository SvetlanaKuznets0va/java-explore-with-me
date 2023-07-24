package ru.practicum.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Integer> ids, Pageable pageable);

    void deleteUserById(int userId);

    User findUserById(int userId);
}
