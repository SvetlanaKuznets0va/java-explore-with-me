package ru.practicum.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.constants.EventSortVariant;
import ru.practicum.constants.State;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.EventModel;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventService {
    EventFullDto privateAddEvent(int userId, NewEventDto newEventDto);

    List<EventShortDto> privateGetEventsByUser(int userId, Pageable pageable);

    EventFullDto privateGetEventByUser(int userId, int eventId);

    EventFullDto privateUpdateEvent(int userId, int eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> adminGetEvents(List<Integer> users, List<State> states, List<Integer> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto adminUpdateEventById(int eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> publicGetEvents(String text, List<Integer> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Boolean onlyAvailable, EventSortVariant sort,
                                        Pageable pageable, HttpServletRequest request);

    EventFullDto publicGetEvent(int id, HttpServletRequest request);

    EventModel findEventModelById(int userId);

    Map<Integer, Long> getViews(List<EventModel> events);
}
