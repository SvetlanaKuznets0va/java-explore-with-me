package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.RequestModel;

@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(RequestModel requestModel) {
        return new ParticipationRequestDto(
                requestModel.getCreated(),
                requestModel.getEvent().getId(),
                requestModel.getId(),
                requestModel.getRequester().getId(),
                requestModel.getStatus()
        );
    }
}
