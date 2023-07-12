package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.EventModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventModel, Integer> {
    List<EventModel> findAllByInitiatorId(int userId, Pageable pageable);

    Optional<EventModel> findByIdAndInitiatorId(int eventId, int userId);
}
