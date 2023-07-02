package ru.practicum.constants;


import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String LDT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter LDT_FORMATTER = DateTimeFormatter.ofPattern(LDT_FORMAT);
}
