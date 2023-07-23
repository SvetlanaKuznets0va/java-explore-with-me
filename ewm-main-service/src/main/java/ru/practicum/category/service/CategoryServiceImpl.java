package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
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
        Category category = categoryRepository.save(CategoryMapper.toCategoryModel(newCategoryDto));
        log.info("Category {} was saved", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(int catId, NewCategoryDto newCategoryDto) {
        Optional<Category> categoryModelOpt = categoryRepository.findById(catId);
        Category category = categoryModelOpt.orElseThrow(() -> throwNfeException(catId));
        category.setName(newCategoryDto.getName());
        categoryRepository.save(category);
        log.info("Category {} was updated to {}", categoryModelOpt.get(), category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(int catId) {
        //TODO добавить проверку на привязку категории к событию
        Category category = categoryRepository.findById(catId).orElseThrow(() -> throwNfeException(catId));
        categoryRepository.deleteById(catId);
        log.info("Category {} was deleted", category);
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
        Category category = categoryRepository.findById(catId).orElseThrow(() -> throwNfeException(catId));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public Category findCategoryById(int catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> throwNfeException(catId));
    }

    private NotFoundException throwNfeException(int catId) {
        return new NotFoundException(String.format("Category with id %d not found", catId));
    }
}
