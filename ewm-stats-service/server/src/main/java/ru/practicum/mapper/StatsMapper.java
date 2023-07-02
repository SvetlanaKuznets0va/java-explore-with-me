package ru.practicum.mapper;

import ru.practicum.dto.HitDto;
import ru.practicum.model.StatsModel;

import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.LDT_FORMATTER;

public class StatsMapper {
    public static StatsModel toStatsModel(HitDto hitDto) {
        return new StatsModel(0,
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                LocalDateTime.parse(hitDto.getTimestamp(), LDT_FORMATTER));
    }

    public static HitDto toStatsDto(StatsModel stats) {
        return new HitDto(stats.getId(),
                stats.getApp(),
                stats.getUri(),
                stats.getIp(),
                stats.getTimestamp().toString());
    }
}
