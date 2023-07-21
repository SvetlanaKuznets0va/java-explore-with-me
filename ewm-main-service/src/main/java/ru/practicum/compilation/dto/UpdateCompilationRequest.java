package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {
    List<Integer> events;
    Boolean pinned;
    @Size(min = 1, max = 50)
    String title;

}
