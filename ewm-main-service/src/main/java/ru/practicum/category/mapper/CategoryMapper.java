package ru.practicum.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

@UtilityClass
public class CategoryMapper {
    public Category toCategoryModel(NewCategoryDto newCategoryDto) {
        return new Category(
                0,
                newCategoryDto.getName()
        );
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}
