package ru.practicum.request.service;

import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequest(int userId, Integer eventId);

    ParticipationRequestDto cancelEventRequest(int userId, int requestId);

    List<ParticipationRequestDto> getRequestsByRequester(int userId);

    List<ParticipationRequestDto> getEventRequestsByEventOwner(int userId, int eventId);

    EventRequestStatusUpdateResult updateEventRequestsByEventOwner(int userId, int eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
