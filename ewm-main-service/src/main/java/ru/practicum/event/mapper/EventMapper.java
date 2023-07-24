package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.constants.AdminStateAction;
import ru.practicum.constants.State;
import ru.practicum.constants.UserStateAction;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ru.practicum.constants.AdminStateAction.PUBLISH_EVENT;
import static ru.practicum.constants.AdminStateAction.REJECT_EVENT;
import static ru.practicum.constants.State.*;
import static ru.practicum.constants.UserStateAction.CANCEL_REVIEW;
import static ru.practicum.constants.UserStateAction.SEND_TO_REVIEW;

@UtilityClass
public class EventMapper {
    public Event toEventModel(NewEventDto newEventDto, User initiator, Category category,
                              Location location) {
        return new Event(
                0,
                newEventDto.getTitle(),
                newEventDto.getAnnotation(),
                category,
                newEventDto.getDescription(),
                newEventDto.isPaid(),
                newEventDto.getParticipantLimit(),
                newEventDto.getEventDate(),
                location,
                LocalDateTime.now(),
                PENDING,
                null,
                initiator,
                newEventDto.isRequestModeration()
        );
    }

    public EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                0,
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                LocationMapper.toLocationDto(event.getLocation()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                0
        );
    }

    public EventFullDto toEventFullDto(Event event, Map<Integer, Long> views, Map<Integer, Integer> confirmedRequests) {
        return new EventFullDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                confirmedRequests.getOrDefault(event.getId(), 0),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                LocationMapper.toLocationDto(event.getLocation()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                views.containsKey(event.getId()) ? views.get(event.getId()) : 0
        );
    }

    public EventShortDto toEventShortDto(Event event, Map<Integer, Long> views) {
        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                0,
                event.getEventDate(),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                views.containsKey(event.getId()) ? views.get(event.getId()) : 0,
                Collections.emptyList()
        );
    }

    public EventShortDto toEventShortDto(Event event, Map<Integer, Long> views, List<CommentDto> comments) {
        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                0,
                event.getEventDate(),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                views.containsKey(event.getId()) ? views.get(event.getId()) : 0,
                comments
        );
    }

    public Event toEventModelUserUpdate(Event event,
                                        UpdateEventUserRequest updateEventUserRequest,
                                        Category category) {
        return new Event(
                event.getId(),
                updateEventUserRequest.getTitle() == null ? event.getTitle() : updateEventUserRequest.getTitle(),
                updateEventUserRequest.getAnnotation() == null ? event.getAnnotation() : updateEventUserRequest.getAnnotation(),
                category == null ? event.getCategory() : category,
                updateEventUserRequest.getDescription() == null ? event.getDescription() : updateEventUserRequest.getDescription(),
                updateEventUserRequest.getPaid() == null ? event.getPaid() : updateEventUserRequest.getPaid(),
                updateEventUserRequest.getParticipantLimit() == null ? event.getParticipantLimit() : updateEventUserRequest.getParticipantLimit(),
                updateEventUserRequest.getEventDate() == null ? event.getEventDate() : updateEventUserRequest.getEventDate(),
                updateEventUserRequest.getLocation() == null ? event.getLocation() : LocationMapper.toLocationModel(updateEventUserRequest.getLocation()),
                event.getCreatedOn(),
                checkUserStateAction(updateEventUserRequest.getStateAction(), event),
                event.getPublishedOn(),
                event.getInitiator(),
                updateEventUserRequest.getRequestModeration() == null ? event.getRequestModeration() : updateEventUserRequest.getRequestModeration()
        );
    }

    public Event toEventModelAdminUpdate(Event em,
                                         UpdateEventAdminRequest uear,
                                         Category cm) {
        State state = checkAdminStateAction(uear.getStateAction(), em);

        return new Event(
                em.getId(),
                uear.getTitle() == null ? em.getTitle() : uear.getTitle(),
                uear.getAnnotation() == null ? em.getAnnotation() : uear.getAnnotation(),
                cm == null ? em.getCategory() : cm,
                uear.getDescription() == null ? em.getDescription() : uear.getDescription(),
                uear.getPaid() == null ? em.getPaid() : uear.getPaid(),
                uear.getParticipantLimit() == null ? em.getParticipantLimit() : uear.getParticipantLimit(),
                uear.getEventDate() == null ? em.getEventDate() : uear.getEventDate(),
                uear.getLocation() == null ? em.getLocation() : new Location(em.getLocation().getId(), uear.getLocation().getLat(), uear.getLocation().getLon()),
                em.getCreatedOn(),
                state,
                state == PUBLISHED ? LocalDateTime.now() : em.getPublishedOn(),
                em.getInitiator(),
                uear.getRequestModeration() == null ? em.getRequestModeration() : uear.getRequestModeration()
        );
    }

    private State checkAdminStateAction(AdminStateAction adminStateAction, Event event) {
        if (adminStateAction != null) {
            if (adminStateAction == PUBLISH_EVENT) {
                return PUBLISHED;
            }
            if (adminStateAction == REJECT_EVENT) {
                return REJECTED;
            }
        }
        return event.getState();
    }

    private State checkUserStateAction(UserStateAction userStateAction, Event event) {
        if (userStateAction != null) {
            if (userStateAction == SEND_TO_REVIEW) {
                return PENDING;
            }
            if (userStateAction == CANCEL_REVIEW) {
                return CANCELED;
            }
        }
        return event.getState();
    }
}
