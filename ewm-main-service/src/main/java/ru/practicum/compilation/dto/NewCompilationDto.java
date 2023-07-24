package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private List<Integer> events = new ArrayList<>();

    private boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50, message = "Incorrect compilation title size")
    private String title;
}
