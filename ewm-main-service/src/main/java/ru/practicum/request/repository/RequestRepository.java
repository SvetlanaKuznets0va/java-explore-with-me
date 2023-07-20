package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.model.RequestModel;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<RequestModel, Integer> {
    Optional<RequestModel> findRequestModelByRequester_IdAndEvent_Id(int requesterId, int eventId);

    Optional<RequestModel> findRequestModelByIdAndRequester_Id(int requestId, int requesterId);

    List<RequestModel> findAllByRequesterId(int requesterId);

    @Query("select count(r.id) " +
            "from RequestModel as r where r.event.id = ?1 and r.status = 'CONFIRMED' group by r.event.id")
    int getConfirmedRequests(int eventId);
}
