package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @Email(message = "Incorrect email")
    @NotBlank(message = "Email is Empty")
    @Size(min = 6, max = 254, message = "Incorrect email size")
    String email;
    @NotBlank(message = "Name is empty")
    @Size(min = 2, max = 250, message = "Incorrect name size")
    String name;
}
