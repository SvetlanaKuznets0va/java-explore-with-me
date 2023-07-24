package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @NotBlank(message = "Category is Empty")
    @Size(min = 1, max = 50, message = "Incorrect category name size")
    String name;
}
