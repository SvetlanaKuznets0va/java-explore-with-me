package ru.practicum.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.model.CompilationModel;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<CompilationModel, Integer> {
    List<CompilationModel> findAllByPinned(boolean pinned, Pageable pageable);
}
