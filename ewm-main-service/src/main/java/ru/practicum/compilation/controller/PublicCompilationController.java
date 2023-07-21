package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constants.Constants.DEFAULT_FROM;
import static ru.practicum.constants.Constants.DEFAULT_SIZE;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/compilations")
@Validated
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAllCompilations(
            @RequestParam(required = false) boolean pinned,
            @RequestParam(required = false, defaultValue = DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = DEFAULT_SIZE) @Positive Integer size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        return compilationService.getAllCompilations(pinned, pageable);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable int compId) {
        return compilationService.getCompilationById(compId);
    }
}
