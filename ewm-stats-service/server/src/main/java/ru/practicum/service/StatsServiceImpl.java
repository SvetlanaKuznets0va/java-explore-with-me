package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Stats;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    @Transactional
    public HitDto add(HitDto hitDto) {
        Stats stats = repository.save(StatsMapper.toStatsModel(hitDto));
        log.info("Stats saved: {}", stats);
        return StatsMapper.toStatsDto(stats);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start == null || end == null || start.isAfter(end) || start.isEqual(end)) {
            throw new ValidationException("Invalid date format");
        }

        if (CollectionUtils.isEmpty(uris) && unique) {
            return repository.getAllStatsWithUniqueIp(start, end);
        }
        if (CollectionUtils.isEmpty(uris) && !unique) {
            return repository.getAllStats(start, end);
        }
        if (unique) {
            return repository.getAllUriStatsWithUniqueIp(start, end, uris);
        }
        return repository.getAllUriStats(start, end, uris);
    }
}
