package ru.practicum.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventService {
    EventFullDto privateAddEvent(int userId, NewEventDto newEventDto);

    List<EventShortDto> privateGetEventsByUser(int userId, Pageable pageable);

    EventFullDto privateGetEventByUser(int userId, int eventId);

    EventFullDto privateUpdateEvent(int userId, int eventId, UpdateEventUserRequest updateEventUserRequest);
}
