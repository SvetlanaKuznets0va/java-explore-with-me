package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.LDT_FORMAT;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @Positive
    private int category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = LDT_FORMAT)
    @DateTimeFormat(pattern = LDT_FORMAT)
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private boolean paid;

    private int participantLimit;

    private boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
