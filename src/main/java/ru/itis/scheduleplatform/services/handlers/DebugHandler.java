package ru.itis.scheduleplatform.services.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.generator.GeneticGenerator;
import ru.itis.scheduleplatform.models.genetic.Schedule;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DebugHandler extends Handler {

    GeneticGenerator geneticGenerator;

    public DebugHandler(GeneticGenerator geneticGenerator) {
        this.geneticGenerator = geneticGenerator;
    }

    @Override
    public List<Schedule> handleRequest(GeneratorParameters generatorParameters, List<ScheduleParameters> scheduleParameters) {
        log.debug("Debug handler processing");
        List<Schedule> schedules = new ArrayList<>();
        for (ScheduleParameters sp : scheduleParameters) {
            schedules.add(geneticGenerator.initGeneration(sp));
        }
        return schedules;
    }
}
