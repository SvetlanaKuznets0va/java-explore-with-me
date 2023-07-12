package ru.practicum.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.CategoryModel;
import ru.practicum.category.service.CategoryService;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.StatsDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.model.EventModel;
import ru.practicum.event.model.LocationModel;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.InvalidDataException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.UserModel;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.constants.State.PUBLISHED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventServiceImpl implements EventService {
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;

    private final StatsClient statsClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    @Transactional
    public EventFullDto privateAddEvent(int userId, NewEventDto newEventDto) {
        checkEventDate(newEventDto.getEventDate());

        UserModel initiator = userService.findUserById(userId);
        CategoryModel category = categoryService.findCategoryById(newEventDto.getCategory());
        LocationModel location = addLocation(newEventDto.getLocation());

        EventModel event = EventMapper.toEventModel(newEventDto, initiator, category, location);

        event = eventRepository.save(event);
        log.info("Event {} was saved", event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> privateGetEventsByUser(int userId, Pageable pageable) {
        userService.findUserById(userId);

        List<EventModel> events = eventRepository.findAllByInitiatorId(userId, pageable);
        Map<Integer, Long> views = getViews(events);
        return events.stream()
                .map(e -> EventMapper.toEventShortDto(e, views))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto privateGetEventByUser(int userId, int eventId) {
        userService.findUserById(userId);
        EventModel event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));
        Map<Integer, Long> views = getViews(Collections.singletonList(event));
        return EventMapper.toEventFullDto(event, views);
    }

    @Override
    @Transactional
    public EventFullDto privateUpdateEvent(int userId, int eventId, UpdateEventUserRequest updateEventUserRequest) {
        checkEventDate(updateEventUserRequest.getEventDate());

        userService.findUserById(userId);
        CategoryModel categoryModel = null;
        if (updateEventUserRequest.getCategory() != null) {
            categoryModel = categoryService.findCategoryById(updateEventUserRequest.getCategory());
        }

        EventModel event = getEventModelByUser(userId, eventId);
        if (event.getState() == PUBLISHED) {
            throw new InvalidDataException("Event can be updated if it is canceled or moderation status");
        }

        EventModel updateEvent = EventMapper.toEventModelUpdate(event, updateEventUserRequest, categoryModel);
        updateEvent = eventRepository.save(updateEvent);
        log.info("Event {} was updated {}", event, updateEvent);
        return EventMapper.toEventFullDto(updateEvent);
    }

    private EventModel getEventModelByUser(int userId, int eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));
    }

    private LocationModel addLocation(LocationDto locationDto) {
        return locationRepository.save(LocationMapper.toLocationModel(locationDto));
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (LocalDateTime.now().plusHours(2).isAfter(eventDate)) {
            throw new InvalidDataException(String.format("Event date %s invalid", eventDate));
        }
    }

    private Map<Integer, Long> getViews(List<EventModel> events) {
        Map<Integer, Long> views = new HashMap<>();

        List<EventModel> publishedOnEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .sorted(Comparator.comparing(EventModel::getPublishedOn))
                .collect(Collectors.toList());

        if (publishedOnEvents.isEmpty()) {
            return views;
        }

        LocalDateTime start = publishedOnEvents.get(0).getPublishedOn();
        LocalDateTime end = publishedOnEvents.get(publishedOnEvents.size() - 1).getPublishedOn();
        List<String> uris = publishedOnEvents.stream()
                .map(EventModel::getId)
                .map(id -> ("/events/" + id))
                .collect(Collectors.toList());

        ResponseEntity<Object> response = statsClient.getStats(start, end, uris, false);

        List<StatsDto> statsDtos;
        try {
            statsDtos = Arrays.asList(mapper.readValue(mapper.writeValueAsString(response.getBody()), StatsDto[].class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        statsDtos.forEach(stat -> {
            Integer eventId = Integer.parseInt(stat.getUri()
                    .split("/", 0)[2]);
            views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
        });

        return views;

    }
}
