package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constants.Constants.DEFAULT_FROM;
import static ru.practicum.constants.Constants.DEFAULT_SIZE;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}/events")
@Validated
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable int userId,
                                 @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Event {} will be added user id={}", newEventDto, userId);
        return eventService.privateAddEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable int userId,
                                               @RequestParam(required = false, defaultValue = DEFAULT_FROM) @PositiveOrZero Integer from,
                                               @RequestParam(required = false, defaultValue = DEFAULT_SIZE) @Positive Integer size) {
        log.info("Requested all events by user id={}", userId);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        return eventService.privateGetEventsByUser(userId, pageable);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUser(@PathVariable int userId, @PathVariable int eventId) {
        log.info("Requested event with id={} by user id={}", eventId, userId);
        return eventService.privateGetEventByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable int userId,
                                    @PathVariable int eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.privateUpdateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequestsByEventOwner(@PathVariable Long userId,
                                                                      @PathVariable Long eventId) {
        return null; //TODO доделать реквесты
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestsByEventOwner(@PathVariable Long userId,
                                                                         @PathVariable Long eventId,
                                                                         @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return null; //TODO доделать реквесты
    }
}
