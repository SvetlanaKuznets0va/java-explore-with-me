package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CountConfirmedRequest {
    private int eventId;
    private long count;
}
