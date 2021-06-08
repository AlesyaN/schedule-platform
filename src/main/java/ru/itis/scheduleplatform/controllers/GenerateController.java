package ru.itis.scheduleplatform.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.NextIterationParameters;
import ru.itis.scheduleplatform.dto.ScheduleMongoPOJO;
import ru.itis.scheduleplatform.dto.ScheduleResponse;
import ru.itis.scheduleplatform.services.DebugService;
import ru.itis.scheduleplatform.services.GenerationService;

import java.util.List;

@CrossOrigin
@RestController
@Slf4j
public class GenerateController {

    private final GenerationService generationService;
    private final DebugService debugService;

    public GenerateController(GenerationService generationService, DebugService debugService) {
        this.generationService = generationService;
        this.debugService = debugService;
    }

    @PostMapping("/generator")
    public List<ScheduleResponse> generate(@RequestBody GeneratorParameters request) {
        log.info("Received POST /generator request: " + request);
        return generationService.process(request);
    }

    @PostMapping("/initGeneration")
    public List<ScheduleResponse> initGeneration(@RequestBody GeneratorParameters request) {
        log.info("Received POST /initGeneration request: " + request);
        return debugService.initGeneration(request);
    }

    @PostMapping("/nextIteration")
    public List<ScheduleResponse> nextIteration(@RequestBody NextIterationParameters request) {
        log.info("Received POST /nextIteration request: " + request);
        return debugService.nextIteration(request.getPopulationIdList());
    }

}
