package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.HitDto;
import ru.practicum.model.Stats;

@UtilityClass
public class StatsMapper {
    public Stats toStatsModel(HitDto hitDto) {
        return new Stats(0,
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTimestamp());
    }

    public HitDto toStatsDto(Stats stats) {
        return new HitDto(stats.getId(),
                stats.getApp(),
                stats.getUri(),
                stats.getIp(),
                stats.getTimestamp());
    }
}
