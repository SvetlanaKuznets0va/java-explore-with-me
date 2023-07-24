package ru.practicum.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(int compId);

    CompilationDto updateCompilation(int compId, UpdateCompilationRequest updateCompilationRequest);

    List<CompilationDto> getAllCompilations(boolean pinned, Pageable pageable);

    CompilationDto getCompilationById(int compId);
}
