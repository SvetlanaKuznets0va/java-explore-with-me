package ru.practicum.event.repository;

import ru.practicum.constants.State;
import ru.practicum.event.model.EventModel;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdditionalRepository {
        List<EventModel> getEventsByAdmin(List<Integer> users, List<State> states,
                                          List<Integer> categories, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, int from, int size);
}
