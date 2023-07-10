package ru.practicum.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.CategoryModel;

@UtilityClass
public class CategoryMapper {
    public CategoryModel toCategoryModel(NewCategoryDto newCategoryDto) {
        return new CategoryModel(
                0,
                newCategoryDto.getName()
        );
    }

    public CategoryDto toCategoryDto(CategoryModel categoryModel) {
        return new CategoryDto(
                categoryModel.getId(),
                categoryModel.getName()
        );
    }
}
