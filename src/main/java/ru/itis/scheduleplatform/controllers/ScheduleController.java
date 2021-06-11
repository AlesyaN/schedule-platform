package ru.itis.scheduleplatform.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.itis.scheduleplatform.dto.SaveParameters;
import ru.itis.scheduleplatform.dto.ScheduleResponse;
import ru.itis.scheduleplatform.dto.ScheduleResponseExtended;
import ru.itis.scheduleplatform.models.ScheduleIds;
import ru.itis.scheduleplatform.services.ScheduleService;

import java.util.List;

@CrossOrigin
@RestController
@Slf4j
public class ScheduleController {

    private ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/schedule")
    public ScheduleIds saveSchedule(@RequestBody SaveParameters saveParameters) {
        log.info("got request " + saveParameters);
        return scheduleService.save(saveParameters);
    }

    @GetMapping("/schedules")
    public List<ScheduleResponseExtended> getSchedules() {
        return scheduleService.getSchedules();
    }

    @GetMapping("/schedule/{id}")
    public ScheduleResponse getSchedule(@PathVariable("id") String id) {
        return scheduleService.getSchedule(id);
    }
}
