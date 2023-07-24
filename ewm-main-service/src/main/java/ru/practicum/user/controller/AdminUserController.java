package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/users")
@Validated
public class AdminUserController {

    private final UserServiceImpl userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("User {}, will be added", newUserRequest);
        return userService.addUser(newUserRequest);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Integer> ids,
                                  @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        log.info("Get Users with parameters ids {}, from {}, size {}", ids, from, size);
        return userService.getUsers(ids, pageable);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable @Positive Integer userId) {
        log.info("User with id {} will be deleted", userId);
        userService.deleteUserById(userId);
    }

}
