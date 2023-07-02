package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {
    private Integer id;
    @NotEmpty
    private String app;
    @NotEmpty
    private String uri;
    @NotEmpty
    private String ip;
    @NotEmpty
    @PastOrPresent
    private String timestamp;
}
