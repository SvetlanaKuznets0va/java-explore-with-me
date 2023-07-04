package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.HitDto;
import ru.practicum.exception.ValidationException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.Constants.LDT_FORMAT;

@RestController
@RequiredArgsConstructor
@RequestMapping()
@Slf4j
@Validated
public class StatsController {

    private final StatsClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> add(@RequestBody @Valid HitDto hitDto) {
        log.info("Saving stats");
        return statsClient.add(hitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam @DateTimeFormat(pattern = LDT_FORMAT) LocalDateTime start,
                             @RequestParam @DateTimeFormat(pattern = LDT_FORMAT) LocalDateTime end,
                             @RequestParam(required = false) List<String> uris,
                             @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Getting stats: start {}, end {}, uris {}, unique {}", start, end, uris, unique);
        try {
            if (start.isAfter(end) || start.isEqual(end)) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new ValidationException("Invalid date format");
        }

        return statsClient.getStats(start, end, uris, unique);
    }
}
