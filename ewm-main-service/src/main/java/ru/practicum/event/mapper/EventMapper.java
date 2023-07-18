package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.CategoryModel;
import ru.practicum.constants.AdminStateAction;
import ru.practicum.constants.State;
import ru.practicum.constants.UserStateAction;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.EventModel;
import ru.practicum.event.model.LocationModel;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.UserModel;

import java.time.LocalDateTime;
import java.util.Map;

import static ru.practicum.constants.AdminStateAction.PUBLISH_EVENT;
import static ru.practicum.constants.AdminStateAction.REJECT_EVENT;
import static ru.practicum.constants.State.*;
import static ru.practicum.constants.UserStateAction.CANCEL_REVIEW;
import static ru.practicum.constants.UserStateAction.SEND_TO_REVIEW;

@UtilityClass
public class EventMapper {
    public EventModel toEventModel(NewEventDto newEventDto, UserModel initiator, CategoryModel category,
                                   LocationModel location) {
        return new EventModel(
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

    public EventFullDto toEventFullDto(EventModel eventModel) {
        return new EventFullDto(
                eventModel.getAnnotation(),
                CategoryMapper.toCategoryDto(eventModel.getCategory()),
                0,
                eventModel.getCreatedOn(),
                eventModel.getDescription(),
                eventModel.getEventDate(),
                eventModel.getId(),
                UserMapper.toUserShortDto(eventModel.getInitiator()),
                LocationMapper.toLocationDto(eventModel.getLocation()),
                eventModel.getPaid(),
                eventModel.getParticipantLimit(),
                eventModel.getPublishedOn(),
                eventModel.getRequestModeration(),
                eventModel.getState(),
                eventModel.getTitle(),
                0
        );
    }

    public EventFullDto toEventFullDto(EventModel eventModel, Map<Integer, Long> views) {
        return new EventFullDto(
                eventModel.getAnnotation(),
                CategoryMapper.toCategoryDto(eventModel.getCategory()),
                0,
                eventModel.getCreatedOn(),
                eventModel.getDescription(),
                eventModel.getEventDate(),
                eventModel.getId(),
                UserMapper.toUserShortDto(eventModel.getInitiator()),
                LocationMapper.toLocationDto(eventModel.getLocation()),
                eventModel.getPaid(),
                eventModel.getParticipantLimit(),
                eventModel.getPublishedOn(),
                eventModel.getRequestModeration(),
                eventModel.getState(),
                eventModel.getTitle(),
                views.containsKey(eventModel.getId()) ? views.get(eventModel.getId()) : 0
        );
    }

    public EventShortDto toEventShortDto(EventModel eventModel, Map<Integer, Long> views) {
        return new EventShortDto(
                eventModel.getAnnotation(),
                CategoryMapper.toCategoryDto(eventModel.getCategory()),
                0,
                eventModel.getEventDate(),
                eventModel.getId(),
                UserMapper.toUserShortDto(eventModel.getInitiator()),
                eventModel.getPaid(),
                eventModel.getTitle(),
                views.containsKey(eventModel.getId()) ? views.get(eventModel.getId()) : 0
        );
    }

    public EventModel toEventModelUserUpdate(EventModel eventModel,
                                             UpdateEventUserRequest updateEventUserRequest,
                                             CategoryModel categoryModel) {
        return new EventModel(
                eventModel.getId(),
                updateEventUserRequest.getTitle() == null ? eventModel.getTitle() : updateEventUserRequest.getTitle(),
                updateEventUserRequest.getAnnotation() == null ? eventModel.getAnnotation() : updateEventUserRequest.getAnnotation(),
                categoryModel == null ? eventModel.getCategory() : categoryModel,
                updateEventUserRequest.getDescription() == null ? eventModel.getDescription() : updateEventUserRequest.getDescription(),
                updateEventUserRequest.getPaid() == null ? eventModel.getPaid() : updateEventUserRequest.getPaid(),
                updateEventUserRequest.getParticipantLimit() == null ? eventModel.getParticipantLimit() : updateEventUserRequest.getParticipantLimit(),
                updateEventUserRequest.getEventDate() == null ? eventModel.getEventDate() : updateEventUserRequest.getEventDate(),
                updateEventUserRequest.getLocation() == null ? eventModel.getLocation() : LocationMapper.toLocationModel(updateEventUserRequest.getLocation()),
                eventModel.getCreatedOn(),
                checkUserStateAction(updateEventUserRequest.getStateAction(), eventModel),
                eventModel.getPublishedOn(),
                eventModel.getInitiator(),
                updateEventUserRequest.getRequestModeration() == null ? eventModel.getRequestModeration() : updateEventUserRequest.getRequestModeration()
        );
    }

    public EventModel toEventModelAdminUpdate(EventModel eventModel,
                                             UpdateEventAdminRequest updateEventAdminRequest,
                                             CategoryModel categoryModel) {
        State state = checkAdminStateAction(updateEventAdminRequest.getStateAction(), eventModel);

        return new EventModel(
                eventModel.getId(),
                updateEventAdminRequest.getTitle() == null ? eventModel.getTitle() : updateEventAdminRequest.getTitle(),
                updateEventAdminRequest.getAnnotation() == null ? eventModel.getAnnotation() : updateEventAdminRequest.getAnnotation(),
                categoryModel == null ? eventModel.getCategory() : categoryModel,
                updateEventAdminRequest.getDescription() == null ? eventModel.getDescription() : updateEventAdminRequest.getDescription(),
                updateEventAdminRequest.getPaid() == null ? eventModel.getPaid() : updateEventAdminRequest.getPaid(),
                updateEventAdminRequest.getParticipantLimit() == null ? eventModel.getParticipantLimit() : updateEventAdminRequest.getParticipantLimit(),
                updateEventAdminRequest.getEventDate() == null ? eventModel.getEventDate() : updateEventAdminRequest.getEventDate(),
                updateEventAdminRequest.getLocation() == null ? eventModel.getLocation() : LocationMapper.toLocationModel(updateEventAdminRequest.getLocation()),
                eventModel.getCreatedOn(),
                state,
                state == PUBLISHED ? LocalDateTime.now() : eventModel.getPublishedOn(),
                eventModel.getInitiator(),
                updateEventAdminRequest.getRequestModeration() == null ? eventModel.getRequestModeration() : updateEventAdminRequest.getRequestModeration()
        );
    }

    private State checkAdminStateAction(AdminStateAction adminStateAction, EventModel eventModel) {
        if (adminStateAction != null) {
            if (adminStateAction == PUBLISH_EVENT) {
                return PUBLISHED;
            }
            if (adminStateAction == REJECT_EVENT) {
                return REJECTED;
            }
        }
        return eventModel.getState();
    }

    private State checkUserStateAction(UserStateAction userStateAction, EventModel eventModel) {
        if (userStateAction != null) {
            if (userStateAction == SEND_TO_REVIEW) {
                return PENDING;
            }
            if (userStateAction == CANCEL_REVIEW) {
                return CANCELED;
            }
        }
        return eventModel.getState();
    }
}
