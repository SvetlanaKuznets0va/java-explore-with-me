package ru.practicum.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.category.model.CategoryModel;
import ru.practicum.category.service.CategoryService;
import ru.practicum.client.StatsClient;
import ru.practicum.constants.EventSortVariant;
import ru.practicum.constants.State;
import ru.practicum.dto.HitDto;
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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.constants.Constants.APP_NAME;
import static ru.practicum.constants.Constants.LDT_FORMAT;
import static ru.practicum.constants.EventSortVariant.EVENT_DATE;
import static ru.practicum.constants.EventSortVariant.VIEWS;
import static ru.practicum.constants.State.PENDING;
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

        EventModel updateEvent = EventMapper.toEventModelUserUpdate(event, updateEventUserRequest, categoryModel);
        updateEvent = eventRepository.save(updateEvent);
        log.info("Event {} was updated {}", event, updateEvent);
        return EventMapper.toEventFullDto(updateEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> adminGetEvents(List<Integer> users, List<State> states, List<Integer> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {
        if (rangeStart.isAfter(rangeEnd) || rangeStart.isEqual(rangeEnd)) {
            throw new InvalidDataException("Invalid date format");
        }

        List<EventModel> events = null;
        if (!(users.isEmpty() && states.isEmpty() && categories.isEmpty())) {
            events = eventRepository.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
        }
        //TODO сделать ифы на другие случаи
        if (CollectionUtils.isEmpty(events)) {
            return Collections.emptyList();
        }
        Map<Integer, Long> views = getViews(events);

        return events.stream()
                .map(e -> EventMapper.toEventFullDto(e, views))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto adminUpdateEventById(int eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getEventDate() != null && updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new InvalidDataException("Event cannot be updated until the start date is less than an hour away");
        }
        CategoryModel categoryModel = null;
        if (updateEventAdminRequest.getCategory() != null) {
            categoryModel = categoryService.findCategoryById(updateEventAdminRequest.getCategory());
        }
        EventModel event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));
        if (event.getState() != PENDING) {
            throw new InvalidDataException("Event cannot be updated it is not pending status");
        }

        EventModel updateEvent = EventMapper.toEventModelAdminUpdate(event, updateEventAdminRequest, categoryModel);
        updateEvent = eventRepository.save(updateEvent);
        log.info("Event {} was updated {} by admin", event, updateEvent);
        return EventMapper.toEventFullDto(updateEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> publicGetEvents(String text, List<Integer> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, EventSortVariant sort,
                                               Pageable pageable, HttpServletRequest request) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.MAX;
        }

        if (rangeStart.isAfter(rangeEnd) || rangeStart.isEqual(rangeEnd)) {
            throw new InvalidDataException("Invalid date format");
        }

        List<EventModel> events = eventRepository.publicGetEvents(PUBLISHED, text, categories, paid, rangeStart, rangeEnd, pageable);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Integer, Long> views = getViews(events);


        if (onlyAvailable) {
            //TODO сделать проверку событий на заполненность
        }

        List<EventShortDto> result = events.stream()
                .map(e -> EventMapper.toEventShortDto(e, views))
                .collect(Collectors.toList());

        if (sort == VIEWS) {
            result.sort(Comparator.comparing(EventShortDto::getViews));
        } else if (sort == EVENT_DATE) {
            result.sort(Comparator.comparing(EventShortDto::getEventDate));
        }

        statsClient.add(new HitDto(0, APP_NAME, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern(LDT_FORMAT)), DateTimeFormatter.ofPattern(LDT_FORMAT))));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto publicGetEvent(int id, HttpServletRequest request) {
        EventModel event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", id)));

        if (event.getState() != PUBLISHED) {
            throw new NotFoundException(String.format("Event with id = %d not found", id));
        }

        statsClient.add(new HitDto(0, APP_NAME, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern(LDT_FORMAT)), DateTimeFormatter.ofPattern(LDT_FORMAT))));

        return EventMapper.toEventFullDto(event);
    }

    private EventModel getEventModelByUser(int userId, int eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));
    }

    private LocationModel addLocation(LocationDto locationDto) {
        return locationRepository.save(LocationMapper.toLocationModel(locationDto));
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate != null && LocalDateTime.now().plusHours(2).isAfter(eventDate)) {
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
