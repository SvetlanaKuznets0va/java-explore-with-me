package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountConfirmedRequest {
    private int eventId;
    private long count;
}
