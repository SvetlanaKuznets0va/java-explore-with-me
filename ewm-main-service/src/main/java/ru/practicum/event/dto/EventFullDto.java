package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.constants.State;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.LDT_FORMAT;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    @JsonFormat(pattern = LDT_FORMAT)
    @DateTimeFormat(pattern = LDT_FORMAT)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = LDT_FORMAT)
    @DateTimeFormat(pattern = LDT_FORMAT)
    private LocalDateTime eventDate;
    private int id;
    private UserShortDto initiator;
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    @JsonFormat(pattern = LDT_FORMAT)
    @DateTimeFormat(pattern = LDT_FORMAT)
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private State state;
    private String title;
    private long views;
}
