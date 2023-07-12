package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.constants.State;

import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.LDT_FORMAT;

@Data
@AllArgsConstructor
public class ParticipationRequestDto {
    @JsonFormat(pattern = LDT_FORMAT)
    @DateTimeFormat(pattern = LDT_FORMAT)
    private LocalDateTime created;
    private int event;
    private int id;
    private int requester;
    private State status;
}
