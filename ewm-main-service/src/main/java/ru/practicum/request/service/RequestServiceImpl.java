package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.constants.RequestStatus;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConflictDataException;
import ru.practicum.exception.InvalidDataException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.constants.RequestStatus.*;
import static ru.practicum.constants.State.PUBLISHED;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(int userId, Integer eventId) {

        User requester = userService.findUserById(userId);
        Event event = eventService.findEventModelById(eventId);

        if (event.getInitiator().getId() == userId) {
            throw new ConflictDataException("You are owner this event, request is invalid");
        }

        if (!event.getState().equals(PUBLISHED)) {
            throw new ConflictDataException("Event not published");
        }

        if (requestRepository.findRequestModelByRequester_IdAndEvent_Id(userId, eventId).isPresent()) {
            throw new ConflictDataException("Attempt to create repeat request");
        }

        Integer confirmedRequests = requestRepository.getConfirmedRequests(eventId);
        if (confirmedRequests != null && confirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictDataException("Threshold for participation in event has been reached");
        }

        Request newRequest = new Request(0, event, requester, LocalDateTime.now(), getStatus(event));

        return RequestMapper.toParticipationRequestDto(requestRepository.save(newRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelEventRequest(int userId, int requestId) {
        Request request = requestRepository.findRequestModelByIdAndRequester_Id(requestId, userId)
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

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequestsByEventOwner(int userId, int eventId) {
        User owner = userService.findUserById(userId);
        Event event = eventService.findEventModelById(eventId);
        if (!Objects.equals(event.getInitiator().getId(), owner.getId())) {
            throw new InvalidDataException("User is not owner");
        }
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestsByEventOwner(int userId, int eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        userService.findUserById(userId);
        Event event = eventService.findEventModelById(eventId);

        if (event.getInitiator().getId() != userId) {
            throw new InvalidDataException("User is not owner");
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0 || eventRequestStatusUpdateRequest.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(Collections.emptyList(), Collections.emptyList());
        }

        List<Request> confirmedList = new ArrayList<>();
        List<Request> rejectedList = new ArrayList<>();

        List<Request> requests = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        if (requests.size() != eventRequestStatusUpdateRequest.getRequestIds().size()) {
            throw new NotFoundException("Count requests does not match");
        }

        if (!requests.stream()
                .map(Request::getStatus)
                .allMatch(RequestStatus.PENDING::equals)) {
            throw new ConflictDataException("Status not valid");
        }

        if (eventRequestStatusUpdateRequest.getStatus().equals(REJECTED)) {
            requests.forEach(request -> request.setStatus(REJECTED));
            requestRepository.saveAll(requests);
            rejectedList.addAll(requests);
        } else {
            Integer confirmedRequests = requestRepository.getConfirmedRequests(eventId);
            if (confirmedRequests != null && confirmedRequests >= event.getParticipantLimit()) {
                throw new ConflictDataException("Threshold for participation in event has been reached");
            }
            requests.forEach(request -> request.setStatus(CONFIRMED));
            requestRepository.saveAll(requests);
            confirmedList.addAll(requests);
        }

        return new EventRequestStatusUpdateResult(
                confirmedList.stream()
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()),
                rejectedList.stream()
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()));
    }

    private RequestStatus getStatus(Event event) {
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return CONFIRMED;
        }
        return PENDING;
    }
}
