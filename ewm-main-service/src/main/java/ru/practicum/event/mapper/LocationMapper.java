package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.Location;

@UtilityClass
public class LocationMapper {
    public Location toLocationModel(LocationDto locationDto) {
        return new Location(
                0,
                locationDto.getLat(),
                locationDto.getLon()
        );
    }

    public LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }
}
