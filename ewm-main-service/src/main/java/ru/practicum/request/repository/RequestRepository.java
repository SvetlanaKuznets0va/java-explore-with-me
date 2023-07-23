package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    Optional<Request> findRequestModelByRequester_IdAndEvent_Id(int requesterId, int eventId);

    Optional<Request> findRequestModelByIdAndRequester_Id(int requestId, int requesterId);

    List<Request> findAllByRequesterId(int requesterId);

    List<Request> findAllByIdIn(List<Integer> requestIds);

    List<Request> findAllByEventId(int eventId);

    @Query("select count(r.id) " +
            "from Request as r where r.event.id = ?1 and r.status = 'CONFIRMED' group by r.event.id")
    Integer getConfirmedRequests(int eventId);
}
