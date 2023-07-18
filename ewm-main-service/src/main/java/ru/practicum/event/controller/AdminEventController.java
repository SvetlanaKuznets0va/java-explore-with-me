package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.constants.State;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.Constants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/events")
@Validated
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> adminGetEvents(
            @RequestParam List<Integer> users,
            @RequestParam List<State> states,
            @RequestParam List<Integer> categories,
            @RequestParam @DateTimeFormat(pattern = LDT_FORMAT) LocalDateTime rangeStart,
            @RequestParam @DateTimeFormat(pattern = LDT_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = DEFAULT_SIZE) @Positive Integer size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        return eventService.adminGetEvents(users, states, categories, rangeStart, rangeEnd, pageable);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable int eventId,
                                          @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.adminUpdateEventById(eventId, updateEventAdminRequest);
    }
}
