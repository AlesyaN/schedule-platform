package ru.itis.scheduleplatform.services.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.generator.Generator;
import ru.itis.scheduleplatform.generator.GeneticGenerator;
import ru.itis.scheduleplatform.generator.SimulatedAnnealingGenerator;
import ru.itis.scheduleplatform.models.genetic.Schedule;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AlgorithmHandler extends Handler {

    private GeneticGenerator geneticGenerator;
    private SimulatedAnnealingGenerator simulatedAnnealingGenerator;

    public AlgorithmHandler(GeneticGenerator geneticGenerator, SimulatedAnnealingGenerator simulatedAnnealingGenerator) {
        this.geneticGenerator = geneticGenerator;
        this.simulatedAnnealingGenerator = simulatedAnnealingGenerator;
    }

    @Override
    public List<Schedule> handleRequest(GeneratorParameters generatorParameters, List<ScheduleParameters> scheduleParameters) {
        Generator generator;
        if (generatorParameters.getAlgorithmType().equals(GeneratorParameters.AlgorithmType.GENETIC)) {
            generator = geneticGenerator;
        } else if (generatorParameters.getAlgorithmType().equals(GeneratorParameters.AlgorithmType.ANNEALING)) {
            generator = simulatedAnnealingGenerator;
        } else {
            throw new IllegalArgumentException("Algorithm type is not supported");
        }

        log.debug("Algorithm handler processing");
        List<Schedule> schedules = new ArrayList<>();
        for (ScheduleParameters sp : scheduleParameters) {
            schedules.add(generator.generate(sp));
        }
        return schedules;
    }
}
