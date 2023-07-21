package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.constants.RequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.LDT_FORMAT;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    @JsonFormat(pattern = LDT_FORMAT)
    @DateTimeFormat(pattern = LDT_FORMAT)
    private LocalDateTime created;
    private int event;
    private int id;
    private int requester;
    private RequestStatus status;
}
