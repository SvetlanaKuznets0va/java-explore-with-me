package ru.practicum.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(int catId, NewCategoryDto newCategoryDto);

    void deleteCategory(int catId);

    List<CategoryDto> getAllCategories(Pageable pageable);

    CategoryDto getCategory(int catId);
}
