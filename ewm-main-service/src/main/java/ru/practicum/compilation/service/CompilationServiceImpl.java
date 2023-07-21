package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.CompilationModel;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.EventModel;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final CompilationRepository compilationRepository;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<EventModel> events = new ArrayList<>();

        if (!newCompilationDto.getEvents().isEmpty()) {
            events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        }

        CompilationModel compilation = compilationRepository.save(
                CompilationMapper.toCompilationModel(newCompilationDto, events));

        Map<Integer, Long> views = eventService.getViews(events);

        List<EventShortDto> eventsShort = events.stream()
                .map(e -> EventMapper.toEventShortDto(e, views))
                .collect(Collectors.toList());
        return CompilationMapper.toCompilationDto(compilation, eventsShort);
    }

    @Override
    @Transactional
    public void deleteCompilationById(int compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));

        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(int compId, UpdateCompilationRequest updateCompilationRequest) {
        List<EventModel> events;
        CompilationModel compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));

        if (updateCompilationRequest.getEvents() != null) {
            events = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
        } else {
            events = compilation.getEvents();
        }

        CompilationModel compilationUpd = compilationRepository.save(
                CompilationMapper.toCompilationModelUpd(compilation, updateCompilationRequest, events));

        Map<Integer, Long> views = eventService.getViews(events);

        List<EventShortDto> eventsShort = events.stream()
                .map(e -> EventMapper.toEventShortDto(e, views))
                .collect(Collectors.toList());
        return CompilationMapper.toCompilationDto(compilationUpd, eventsShort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAllCompilations(boolean pinned, Pageable pageable) {
        List<CompilationModel> compilations = pinned ?
                compilationRepository.findAllByPinned(true, pageable) : compilationRepository.findAll(pageable).toList();

        return compilations.stream()
                .map(c -> {
                    Map<Integer, Long> views = eventService.getViews(c.getEvents());

                    List<EventShortDto> eventsShort = c.getEvents().stream()
                            .map(e -> EventMapper.toEventShortDto(e, views))
                            .collect(Collectors.toList());
                    return CompilationMapper.toCompilationDto(c, eventsShort);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(int compId) {
        CompilationModel compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));

        Map<Integer, Long> views = eventService.getViews(compilation.getEvents());

        List<EventShortDto> eventsShort = compilation.getEvents().stream()
                .map(e -> EventMapper.toEventShortDto(e, views))
                .collect(Collectors.toList());
        return CompilationMapper.toCompilationDto(compilation, eventsShort);
    }
}
