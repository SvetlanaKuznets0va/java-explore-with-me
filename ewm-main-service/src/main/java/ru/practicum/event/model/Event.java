package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.category.model.Category;
import ru.practicum.constants.State;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.LDT_FORMAT;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String title;

    String annotation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    Category category;

    String description;

    Boolean paid;

    Integer participantLimit;

    @DateTimeFormat(pattern = LDT_FORMAT)
    LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    Location location;

    @DateTimeFormat(pattern = LDT_FORMAT)
    LocalDateTime createdOn;

    @Enumerated(EnumType.STRING)
    State state;

    @DateTimeFormat(pattern = LDT_FORMAT)
    LocalDateTime publishedOn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    User initiator;

    Boolean requestModeration;
}
