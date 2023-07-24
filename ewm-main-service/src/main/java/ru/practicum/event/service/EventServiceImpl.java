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
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.client.StatsClient;
import ru.practicum.constants.EventSortingOption;
import ru.practicum.constants.State;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.CommentMapper;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.CommentRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.ConflictDataException;
import ru.practicum.exception.InvalidDataException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.constants.Constants.APP_NAME;
import static ru.practicum.constants.Constants.LDT_FORMAT;
import static ru.practicum.constants.EventSortingOption.EVENT_DATE;
import static ru.practicum.constants.EventSortingOption.VIEWS;
import static ru.practicum.constants.State.PENDING;
import static ru.practicum.constants.State.PUBLISHED;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;

    private final StatsClient statsClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    @Transactional
    public EventFullDto privateAddEvent(int userId, NewEventDto newEventDto) {
        checkEventDate(newEventDto.getEventDate());

        User initiator = userService.findUserById(userId);
        Category category = categoryService.findCategoryById(newEventDto.getCategory());
        Location location = addLocation(newEventDto.getLocation());

        Event event = EventMapper.toEventModel(newEventDto, initiator, category, location);

        event = eventRepository.save(event);
        log.info("Event {} was saved", event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> privateGetEventsByUser(int userId, Pageable pageable) {
        userService.findUserById(userId);

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        Map<Integer, Long> views = getViews(events);
        Map<Integer, List<CommentDto>> commentsDto = getCommentsDto(events);
        return events.stream()
                .map(e -> EventMapper.toEventShortDto(e, views, commentsDto.get(e.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto privateGetEventByUser(int userId, int eventId) {
        userService.findUserById(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));
        Map<Integer, Long> views = getViews(Collections.singletonList(event));
        Map<Integer, Integer> confirmedRequests = getConfirmedRequests(Collections.singletonList(event));
        return EventMapper.toEventFullDto(event, views, confirmedRequests);
    }

    @Override
    @Transactional
    public EventFullDto privateUpdateEvent(int userId, int eventId, UpdateEventUserRequest updateEventUserRequest) {
        checkEventDate(updateEventUserRequest.getEventDate());

        userService.findUserById(userId);
        Category category = null;
        if (updateEventUserRequest.getCategory() != null) {
            category = categoryService.findCategoryById(updateEventUserRequest.getCategory());
        }

        Event event = getEventModelByUser(userId, eventId);
        if (event.getState() == PUBLISHED) {
            throw new ConflictDataException("Event can be updated if it is canceled or moderation status");
        }

        Event updateEvent = EventMapper.toEventModelUserUpdate(event, updateEventUserRequest, category);
        updateEvent = eventRepository.save(updateEvent);
        log.info("Event {} was updated {}", event, updateEvent);
        return EventMapper.toEventFullDto(updateEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> adminGetEvents(List<Integer> users, List<State> states, List<Integer> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        if (rangeStart != null && rangeEnd != null && (rangeStart.isAfter(rangeEnd) || rangeStart.isEqual(rangeEnd))) {
            throw new InvalidDataException("Invalid date format");
        }

        List<Event> events = eventRepository.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);

        if (CollectionUtils.isEmpty(events)) {
            return Collections.emptyList();
        }
        Map<Integer, Long> views = getViews(events);
        Map<Integer, Integer> confirmedRequests = getConfirmedRequests(events);
        return events.stream()
                .map(e -> EventMapper.toEventFullDto(e, views, confirmedRequests))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto adminUpdateEventById(int eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getEventDate() != null && updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Event cannot be updated until the start date is less than an hour away");
        }
        Category category = null;
        if (updateEventAdminRequest.getCategory() != null) {
            category = categoryService.findCategoryById(updateEventAdminRequest.getCategory());
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));
        if (event.getState() != PENDING) {
            throw new ConflictDataException("Event cannot be updated it is not pending status");
        }

        Event updateEvent = EventMapper.toEventModelAdminUpdate(event, updateEventAdminRequest, category);
        updateEvent = eventRepository.save(updateEvent);
        log.info("Event {} was updated {} by admin", event, updateEvent);
        return EventMapper.toEventFullDto(updateEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> publicGetEvents(String text, List<Integer> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, EventSortingOption sort,
                                               int from, int size, HttpServletRequest request) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        if (rangeStart.isAfter(rangeEnd) || rangeStart.isEqual(rangeEnd)) {
            throw new ValidationException("Invalid date format");
        }

        List<Event> events = eventRepository.publicGetEvents(PUBLISHED, text, categories, paid, rangeStart, rangeEnd, from, size);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Integer, Long> views = getViews(events);


        if (onlyAvailable) {
            events = events.stream()
                    .filter(e -> (e.getParticipantLimit() == null ||
                            e.getParticipantLimit() >= requestRepository.getConfirmedRequests(e.getId())))
                    .collect(Collectors.toList());
        }

        Map<Integer, List<CommentDto>> comments = getCommentsDto(events);

        List<EventShortDto> result = events.stream()
                .map(e -> EventMapper.toEventShortDto(e, views, comments.get(e.getId())))
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
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", id)));

        if (event.getState() != PUBLISHED) {
            throw new NotFoundException(String.format("Event with id = %d not found", id));
        }

        statsClient.add(new HitDto(0, APP_NAME, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern(LDT_FORMAT)), DateTimeFormatter.ofPattern(LDT_FORMAT))));
        Map<Integer, Long> views = getViews(Collections.singletonList(event));
        Map<Integer, Integer> confirmedRequests = getConfirmedRequests(Collections.singletonList(event));
        return EventMapper.toEventFullDto(event, views, confirmedRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public Event findEventModelById(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));
    }

    @Override
    public Map<Integer, Long> getViews(List<Event> events) {
        Map<Integer, Long> views = new HashMap<>();

        List<Event> publishedOnEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .sorted(Comparator.comparing(Event::getPublishedOn))
                .collect(Collectors.toList());

        if (publishedOnEvents.isEmpty()) {
            return views;
        }

        LocalDateTime start = publishedOnEvents.get(0).getPublishedOn();
        LocalDateTime end = publishedOnEvents.get(publishedOnEvents.size() - 1).getPublishedOn().plusSeconds(1);
        List<String> uris = publishedOnEvents.stream()
                .map(Event::getId)
                .map(id -> ("/events/" + id))
                .collect(Collectors.toList());

        ResponseEntity<Object> response = statsClient.getStats(start, end, uris, true);

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

    @Override
    @Transactional
    public CommentDto addComment(int userId, int eventId, CommentDto text) {
        User user = userService.findUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));

        if (event.getState() != PUBLISHED) {
            throw new InvalidDataException("Event is not published");
        }

        Comment comment = commentRepository.save(CommentMapper.toComment(eventId, userId, text));
        log.info("Comment was saved for event id={}", eventId);
        return CommentMapper.toCommentDto(comment, user.getName());
    }

    @Override
    @Transactional
    public CommentDto updateComment(int userId, int eventId, int commentId, CommentDto text) {
        User user = userService.findUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));

        if (event.getState() != PUBLISHED) {
            throw new InvalidDataException("Event is not published");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        comment.setText(text.getText());

        comment = commentRepository.save(comment);
        log.info("Comment was updated for event id={}", eventId);
        return CommentMapper.toCommentDto(comment, user.getName());
    }

    @Override
    @Transactional
    public void deleteComment(int eventId, int commentId) {
        commentRepository.deleteById(commentId);
        log.info("Comment was deleted for event id={}", eventId);
    }

    private Map<Integer, List<CommentDto>> getCommentsDto(List<Event> events) {
        List<Integer> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findCommentsByEventIdIn(eventIds);
        Set<Integer> userIds = comments.stream().map(Comment::getUserId).collect(Collectors.toSet());
        List<User> users = userService.findUsersById(new ArrayList<>(userIds));
        Map<Integer, String> userIdToNameMap = users.stream().collect(Collectors.toMap(User::getId, User::getName));

        Function<Comment, CommentDto> translate = c -> {
            String name = userIdToNameMap.get(c.getUserId());
                   return CommentMapper.toCommentDto(c, name);
        };

        return comments.stream().collect(Collectors.groupingBy(Comment::getEventId,
                Collectors.mapping(translate, Collectors.toList())));
    }

    private Map<Integer, Integer> getConfirmedRequests(List<Event> events) {
        return events.stream()
                .filter(e -> e.getPublishedOn() != null)
                .collect(Collectors.toMap(Event::getId, e -> {
                    Integer cr = requestRepository.getConfirmedRequests(e.getId());
                    return cr == null ? 0 : cr;
                }));
    }

    private Event getEventModelByUser(int userId, int eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));
    }

    private Location addLocation(LocationDto locationDto) {
        return locationRepository.save(LocationMapper.toLocationModel(locationDto));
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate != null && LocalDateTime.now().plusHours(2).isAfter(eventDate)) {
            throw new ValidationException(String.format("Event date %s invalid", eventDate));
        }
    }
}
