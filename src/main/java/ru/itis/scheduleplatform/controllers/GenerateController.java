package ru.itis.scheduleplatform.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.NextIterationParameters;
import ru.itis.scheduleplatform.dto.ScheduleResponse;
import ru.itis.scheduleplatform.services.DebugService;
import ru.itis.scheduleplatform.services.GenerationService;
import ru.itis.scheduleplatform.services.InputDataService;

import java.util.List;

@CrossOrigin
@RestController
@Slf4j
public class GenerateController {

    private final GenerationService generationService;
    private final DebugService debugService;
    private final InputDataService inputDataService;

    public GenerateController(GenerationService generationService, DebugService debugService, InputDataService inputDataService) {
        this.generationService = generationService;
        this.debugService = debugService;
        this.inputDataService = inputDataService;
    }

    @PostMapping("/generator")
    public List<ScheduleResponse> generate(@RequestBody GeneratorParameters request) {
        log.info("Received POST /generator request: " + request);
        inputDataService.saveInputData(request.getInputData());
        return generationService.process(request);
    }

    @PostMapping("/initGeneration")
    public List<ScheduleResponse> initGeneration(@RequestBody GeneratorParameters request) {
        log.info("Received POST /initGeneration request: " + request);
        inputDataService.saveInputData(request.getInputData());
        return debugService.initGeneration(request);
    }

    @PostMapping("/nextIteration")
    public List<ScheduleResponse> nextIteration(@RequestBody NextIterationParameters request) {
        log.info("Received POST /nextIteration request: " + request);
        return debugService.nextIteration(request.getPopulationIdList());
    }

}
