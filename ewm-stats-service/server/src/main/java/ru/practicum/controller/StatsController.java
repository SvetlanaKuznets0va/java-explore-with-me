package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.service.StatsService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping()
@Slf4j
public class StatsController {

    private StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public HitDto add(@RequestBody HitDto hitDto) {
        log.info("Saving stats");
        return statsService.add(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam String start,
                             @RequestParam String end,
                             @RequestParam(required = false) List<String> uris,
                             @RequestParam(required = false, defaultValue = "false") boolean unique) {
        log.info("Getting stats: start {}, end {}, uris {}, unique {}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }
}
