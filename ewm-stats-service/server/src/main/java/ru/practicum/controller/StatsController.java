package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.Constants.LDT_FORMAT;

@RestController
@RequiredArgsConstructor
@RequestMapping()
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto add(@RequestBody @Valid HitDto hitDto) {
        log.info("Saving stats");
        return statsService.add(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam(required = false) @DateTimeFormat(pattern = LDT_FORMAT) LocalDateTime start,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = LDT_FORMAT) LocalDateTime end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Getting stats: start {}, end {}, uris {}, unique {}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }
}
