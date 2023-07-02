package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.StatsModel;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.Constants.LDT_FORMATTER;

@Service
@Slf4j
public class StatsServiceImpl implements StatsService {

    private StatsRepository repository;

    public StatsServiceImpl(StatsRepository repository) {
        this.repository = repository;
    }

    @Override
    public HitDto add(HitDto hitDto) {
        StatsModel statsModel = repository.save(StatsMapper.toStatsModel(hitDto));
        log.info("Stats saved: {}", statsModel);
        return StatsMapper.toStatsDto(statsModel);
    }

    @Override
    public List<StatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startF = LocalDateTime.parse(start, LDT_FORMATTER);
        LocalDateTime endF = LocalDateTime.parse(end, LDT_FORMATTER);

        if (CollectionUtils.isEmpty(uris) && unique) {
            return repository.getAllStatsWithUniqueIp(startF, endF);
        }
        if (CollectionUtils.isEmpty(uris) && !unique) {
            return repository.getAllStats(startF, endF);
        }
        if (unique) {
            return repository.getAllUriStatsWithUniqueIp(startF, endF, uris);
        }
        if (!unique) {
            return repository.getAllUriStats(startF, endF, uris);
        }
        return null;
    }
}
