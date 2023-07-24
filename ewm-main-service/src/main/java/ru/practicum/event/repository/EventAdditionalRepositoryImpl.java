package ru.practicum.event.repository;

import org.springframework.util.CollectionUtils;
import ru.practicum.constants.State;
import ru.practicum.event.model.Event;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

public class EventAdditionalRepositoryImpl implements EventAdditionalRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Event> getEventsByAdmin(List<Integer> users, List<State> states,
                                        List<Integer> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, int from, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        Predicate predicate = cb.conjunction();

        if (!CollectionUtils.isEmpty(users)) {
            predicate = cb.and(predicate, root.get("initiator").in(users));
        }
        if (!CollectionUtils.isEmpty(states)) {
            predicate = cb.and(predicate, root.get("state").in(states));
        }
        if (!CollectionUtils.isEmpty(categories)) {
            predicate = cb.and(predicate, root.get("category").in(categories));
        }
        if (rangeStart != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        query.select(root).where(predicate);
        return em.createQuery(query).setFirstResult(from).setMaxResults(size).getResultList();
    }

    @Override
    public List<Event> publicGetEvents(State state, String text,
                                       List<Integer> categories, Boolean paid,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                       int from, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        Predicate predicate = cb.conjunction();

        if (text != null && !text.isBlank()) {
            Predicate annotation = cb.like(cb.lower(root.get("annotation")), "%" + text.toLowerCase() + "%");
            Predicate description = cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%");
            predicate = cb.and(predicate, cb.or(annotation, description));
        }
        if (!CollectionUtils.isEmpty(categories)) {
            predicate = cb.and(predicate, root.get("category").in(categories));
        }
        if (paid != null) {
            predicate = cb.and(predicate, root.get("paid").in(paid));
        }
        if (rangeStart == null && rangeEnd == null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("eventDate"), LocalDateTime.now()));
        } else {
            if (rangeStart != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }
            if (rangeEnd != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }
        }
        predicate = cb.and(predicate, root.get("state").in(state));

        query.select(root).where(predicate);
        return em.createQuery(query).setFirstResult(from).setMaxResults(size).getResultList();
    }
}

