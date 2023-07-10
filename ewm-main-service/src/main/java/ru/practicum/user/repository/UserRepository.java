package ru.practicum.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.user.model.UserModel;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {
    List<UserModel> findAllByIdIn(List<Integer> ids, Pageable pageable);
}
