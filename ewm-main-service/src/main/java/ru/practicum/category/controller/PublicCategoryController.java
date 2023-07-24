package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constants.Constants.DEFAULT_FROM;
import static ru.practicum.constants.Constants.DEFAULT_SIZE;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/categories")
@Validated
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(
            @RequestParam(required = false, defaultValue = DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = DEFAULT_SIZE) @Positive Integer size) {
        log.info("Requested all categories");
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        return categoryService.getAllCategories(pageable);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable @Positive int catId) {
        log.info("Requested category with id {}", catId);
        return categoryService.getCategory(catId);
    }
}
