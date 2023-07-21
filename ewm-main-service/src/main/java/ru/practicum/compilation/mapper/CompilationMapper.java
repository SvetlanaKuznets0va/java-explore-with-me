package ru.practicum.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.CompilationModel;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.EventModel;

import java.util.List;

@UtilityClass
public class CompilationMapper {
    public CompilationModel toCompilationModel(NewCompilationDto ncd, List<EventModel> events) {
        return new CompilationModel(
                0,
                ncd.getTitle(),
                ncd.isPinned(),
                events
        );
    }

    public CompilationModel toCompilationModelUpd(CompilationModel cm, UpdateCompilationRequest ucr, List<EventModel> events) {
        return new CompilationModel(
                cm.getId(),
                ucr.getTitle() == null ? cm.getTitle() : ucr.getTitle(),
                ucr.getPinned() == null ? cm.getPinned() : ucr.getPinned(),
                events == null ? cm.getEvents() : events
        );
    }

    public CompilationDto toCompilationDto(CompilationModel cm, List<EventShortDto> es) {
        return new CompilationDto(
                es,
                cm.getId(),
                cm.getPinned(),
                cm.getTitle()
        );
    }
}
