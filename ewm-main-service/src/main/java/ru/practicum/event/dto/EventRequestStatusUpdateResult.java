package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@Data
@AllArgsConstructor
public class EventRequestStatusUpdateResult {
    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;
}
