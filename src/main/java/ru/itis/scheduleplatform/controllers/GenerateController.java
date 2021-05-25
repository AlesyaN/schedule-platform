package ru.itis.scheduleplatform.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.ScheduleResponse;
import ru.itis.scheduleplatform.services.GenerationService;

@RestController
@Slf4j
public class GenerateController {

    private final GenerationService generationService;

    public GenerateController(GenerationService generationService) {
        this.generationService = generationService;
    }

    @PostMapping("/generator")
    public ScheduleResponse generate(@RequestBody GeneratorParameters request) {
        log.info("Received POST request:" + request);
        return generationService.process(request);
    }

}
