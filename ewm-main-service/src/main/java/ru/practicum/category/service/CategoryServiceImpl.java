package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.CategoryModel;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        CategoryModel categoryModel = categoryRepository.save(CategoryMapper.toCategoryModel(newCategoryDto));
        log.info("Category {} was saved", categoryModel);
        return CategoryMapper.toCategoryDto(categoryModel);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(int catId, NewCategoryDto newCategoryDto) {
        Optional<CategoryModel> categoryModelOpt = categoryRepository.findById(catId);
        CategoryModel categoryModel = categoryModelOpt.orElseThrow(() -> nfeException(catId));
        categoryModel.setName(newCategoryDto.getName());
        categoryRepository.save(categoryModel);
        log.info("Category {} was updated to {}", categoryModelOpt.get(), categoryModel);
        return CategoryMapper.toCategoryDto(categoryModel);
    }

    @Override
    @Transactional
    public void deleteCategory(int catId) {
        //TODO добавить проверку на привязку категории к событию
        CategoryModel categoryModel = categoryRepository.findById(catId).orElseThrow(() -> nfeException(catId));
        categoryRepository.deleteById(catId);
        log.info("Category {} was deleted", categoryModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(int catId) {
        CategoryModel categoryModel = categoryRepository.findById(catId).orElseThrow(() -> nfeException(catId));
        return CategoryMapper.toCategoryDto(categoryModel);
    }

    @Override
    public CategoryModel findCategoryById(int catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> nfeException(catId));
    }

    private NotFoundException nfeException(int catId) {
        return new NotFoundException(String.format("Category with id %d not found", catId));
    }
}
