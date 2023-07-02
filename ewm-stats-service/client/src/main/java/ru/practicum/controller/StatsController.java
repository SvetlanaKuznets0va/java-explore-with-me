package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.HitDto;
import ru.practicum.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.Constants.LDT_FORMATTER;

@RestController
@RequestMapping()
@Slf4j
public class StatsController {

    private StatsClient statsClient;

    @Autowired
    public StatsController(StatsClient statsClient) {
        this.statsClient = statsClient;
    }

    @PostMapping("/hit")
    public ResponseEntity<Object> add(@RequestBody @Valid HitDto hitDto) {
        log.info("Saving stats");
        return statsClient.add(hitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam @NotEmpty String start,
                             @RequestParam @NotEmpty String end,
                             @RequestParam(required = false) List<String> uris,
                             @RequestParam(required = false, defaultValue = "false") boolean unique) {
        log.info("Getting stats: start {}, end {}, uris {}, unique {}", start, end, uris, unique);
        try {
            LocalDateTime startF = LocalDateTime.parse(start, LDT_FORMATTER);
            LocalDateTime endF = LocalDateTime.parse(end, LDT_FORMATTER);
            if (startF.isAfter(endF) || startF.isEqual(endF)) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new ValidationException("Invalid date format");
        }

        return statsClient.getStats(start, end, uris, unique);
    }
}
