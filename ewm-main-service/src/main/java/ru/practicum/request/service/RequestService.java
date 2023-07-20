package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequest(int userId, int eventId);

    ParticipationRequestDto cancelEventRequest(int userId, int requestId);

    List<ParticipationRequestDto> getRequestsByRequester(int userId);
}
