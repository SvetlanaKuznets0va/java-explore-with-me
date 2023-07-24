package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Integer> {

    @Query("select new ru.practicum.dto.StatsDto(s.app, s.uri, count(distinct s.ip)) from Stats as s " +
            "where s.timestamp between ?1 and ?2 " +
            "group by s.app, s.uri order by count(distinct s.ip) desc ")
    List<StatsDto> getAllStatsWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.StatsDto(s.app, s.uri, count(s.ip)) from Stats as s " +
            "where s.timestamp between ?1 and ?2 " +
            "group by s.app, s.uri order by count(s.ip) desc")
    List<StatsDto> getAllStats(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.StatsDto(s.app, s.uri, count(distinct s.ip)) from Stats as s " +
            "where s.timestamp between ?1 and ?2 and s.uri in (?3)" +
            "group by s.app, s.uri order by count(distinct s.ip) desc")
    List<StatsDto> getAllUriStatsWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.StatsDto(s.app, s.uri, count(s.ip)) from Stats as s " +
            "where s.timestamp between ?1 and ?2 and s.uri in (?3)" +
            "group by s.app, s.uri order by count(s.ip) desc")
    List<StatsDto> getAllUriStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
