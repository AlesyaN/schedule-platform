package ru.itis.scheduleplatform.services.handlers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.generator.Generator;
import ru.itis.scheduleplatform.generator.GeneticGenerator;
import ru.itis.scheduleplatform.generator.SimulatedAnnealingGenerator;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.Group;
import ru.itis.scheduleplatform.models.ScheduleCell;
import ru.itis.scheduleplatform.models.genetic.Schedule;

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
    public Schedule handleRequest(GeneratorParameters generatorParameters, List<ScheduleParameters> scheduleParameters) {
        Generator generator;
        if (generatorParameters.getAlgorithmType().equals(GeneratorParameters.AlgorithmType.GENETIC)) {
            generator = geneticGenerator;
        } else if (generatorParameters.getAlgorithmType().equals(GeneratorParameters.AlgorithmType.ANNEALING)) {
            generator = simulatedAnnealingGenerator;
        } else {
            throw new IllegalArgumentException("Algorithm type is not supported");
        }

        log.debug("Algorithm handler processing");
        Table<ScheduleCell, Group, Class> scheduleTable = HashBasedTable.create();
        for (ScheduleParameters sp : scheduleParameters) {
            scheduleTable.putAll(generator.generate(sp));
        }
        return new Schedule(scheduleTable);
    }
}
