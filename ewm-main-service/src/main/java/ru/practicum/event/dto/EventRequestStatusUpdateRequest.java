package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.constants.NewEventRequestStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    @NotEmpty
    List<Long> requestIds;

    @NotNull
    NewEventRequestStatus status;
}
