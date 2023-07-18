package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.constants.State;
import ru.practicum.event.model.EventModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventModel, Integer> {
    List<EventModel> findAllByInitiatorId(int userId, Pageable pageable);

    Optional<EventModel> findByIdAndInitiatorId(int eventId, int userId);

    @Query("select em from EventModel as em " +
            "where em.initiator.id in ?1 and em.state in ?2 and em.category.id in ?3 " +
            "and (em.eventDate between ?4 and ?5)")
    List<EventModel> getEventsByAdmin(List<Integer> users, List<State> states,
                                      List<Integer> categories, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, Pageable pageable);

    @Query("select em from EventModel as em " +
            "where em.state = ?1 " +
            "and (lower(em.annotation) like lower((CONCAT('%', ?2, '%'))) " +
            "or lower(em.description) like lower((CONCAT('%', ?2, '%')))) " +
            "and em.category.id in ?3 " +
            "and em.paid = ?4 " +
            "and (em.eventDate between ?5 and ?6)")
    List<EventModel> publicGetEvents(State state, String text, List<Integer> categories, Boolean paid,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);
}
