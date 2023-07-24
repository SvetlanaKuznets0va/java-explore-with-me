package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
            User user = userRepository.save(UserMapper.toUserModel(newUserRequest));
            log.info("Saved user {}", user);
            return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Integer> ids, Pageable pageable) {
        if (CollectionUtils.isEmpty(ids)) {
            log.info("Return all users pageable {}", pageable);
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }

        log.info("Return users by ids {} , pageable {}", ids, pageable);
        return userRepository.findAllByIdIn(ids, pageable).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
        log.info("User with id {} was deleted", userId);
    }

    @Override
    @Transactional
    public User findUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional
    public List<User> findUsersById(List<Integer> ids) {
        return userRepository.findAllByIdIn(ids);
    }
}
