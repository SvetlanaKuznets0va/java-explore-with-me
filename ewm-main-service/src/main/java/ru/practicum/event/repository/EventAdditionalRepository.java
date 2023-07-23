package ru.practicum.event.repository;

import ru.practicum.constants.State;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdditionalRepository {
        List<Event> getEventsByAdmin(List<Integer> users, List<State> states,
                                     List<Integer> categories, LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd, int from, int size);

        List<Event> publicGetEvents(State state, String text, List<Integer> categories, Boolean paid,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);
}
