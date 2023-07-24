package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.constants.AdminStateAction;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.LDT_FORMAT;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    @Positive
    private Integer category;
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = LDT_FORMAT)
    @DateTimeFormat(pattern = LDT_FORMAT)
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private AdminStateAction stateAction;
    @Size(min = 3, max = 120)
    private String title;
}
