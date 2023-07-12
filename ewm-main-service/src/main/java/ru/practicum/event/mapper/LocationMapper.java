package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.LocationModel;

@UtilityClass
public class LocationMapper {
    public LocationModel toLocationModel(LocationDto locationDto) {
        return new LocationModel(
                0,
                locationDto.getLat(),
                locationDto.getLon()
        );
    }

    public LocationDto toLocationDto(LocationModel locationModel) {
        return new LocationDto(
                locationModel.getLat(),
                locationModel.getLon()
        );
    }
}
