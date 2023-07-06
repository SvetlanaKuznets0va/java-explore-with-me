package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.HitDto;
import ru.practicum.model.StatsModel;

@UtilityClass
public class StatsMapper {
    public StatsModel toStatsModel(HitDto hitDto) {
        return new StatsModel(0,
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTimestamp());
    }

    public HitDto toStatsDto(StatsModel stats) {
        return new HitDto(stats.getId(),
                stats.getApp(),
                stats.getUri(),
                stats.getIp(),
                stats.getTimestamp());
    }
}
