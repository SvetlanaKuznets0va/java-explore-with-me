package ru.practicum.request.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.constants.RequestStatus;
import ru.practicum.event.model.EventModel;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.InvalidDataException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.RequestModel;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.UserModel;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.constants.RequestStatus.*;
import static ru.practicum.constants.State.PUBLISHED;

@Service
public class RequestServiceImpl implements RequestService {
    RequestRepository requestRepository;
    UserService userService;
    EventService eventService;

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(int userId, int eventId) {
        UserModel requester = userService.findUserById(userId);
        EventModel event = eventService.findEventModelById(eventId);

        if (event.getInitiator().getId() == userId) {
            throw new InvalidDataException("You are owner this event, request is invalid");
        }

        if (!event.getState().equals(PUBLISHED)) {
            throw new InvalidDataException("Event not published");
        }

        if (requestRepository.findRequestModelByRequester_IdAndEvent_Id(userId, eventId).isPresent()) {
            throw new InvalidDataException("Attempt to create repeat request");
        }

        if (requestRepository.getConfirmedRequests(eventId) > event.getParticipantLimit()) {
            throw new InvalidDataException("Threshold for participation in event has been reached");
        }

        RequestModel newRequest = new RequestModel(0, event, requester, LocalDateTime.now(), getStatus(event));

        return RequestMapper.toParticipationRequestDto(requestRepository.save(newRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelEventRequest(int userId, int requestId) {
        RequestModel request = requestRepository.findRequestModelByIdAndRequester_Id(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Cancellation request not found"));

        request.setStatus(CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByRequester(int userId) {
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    private RequestStatus getStatus(EventModel event) {
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return CONFIRMED;
        }
        return PENDING;
    }
}
