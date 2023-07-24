package ru.practicum.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.util.List;

@UtilityClass
public class CompilationMapper {
    public Compilation toCompilationModel(NewCompilationDto ncd, List<Event> events) {
        return new Compilation(
                0,
                ncd.getTitle(),
                ncd.isPinned(),
                events
        );
    }

    public Compilation toCompilationModelUpd(Compilation cm, UpdateCompilationRequest ucr, List<Event> events) {
        return new Compilation(
                cm.getId(),
                ucr.getTitle() == null ? cm.getTitle() : ucr.getTitle(),
                ucr.getPinned() == null ? cm.getPinned() : ucr.getPinned(),
                events == null ? cm.getEvents() : events
        );
    }

    public CompilationDto toCompilationDto(Compilation cm, List<EventShortDto> es) {
        return new CompilationDto(
                es,
                cm.getId(),
                cm.getPinned(),
                cm.getTitle()
        );
    }
}
