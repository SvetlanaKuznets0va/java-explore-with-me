package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}/requests")
@Validated
public class PrivateRequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable int userId,
                                              @RequestParam (required = false) @NotNull Integer eventId) {
        log.info("Request by user id={} on event id={} will be added", userId, eventId);
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelEventRequest(@PathVariable int userId,
                                                      @PathVariable int requestId) {
        return requestService.cancelEventRequest(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getRequestsByRequester(@PathVariable int userId) {
        return requestService.getRequestsByRequester(userId);
    }
}
