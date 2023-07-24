package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, EventAdditionalRepository {
    List<Event> findAllByInitiatorId(int userId, Pageable pageable);

    List<Event> findAllByIdIn(List<Integer> eventIds);

    Optional<Event> findByIdAndInitiatorId(int eventId, int userId);
}
