package ru.itis.scheduleplatform;

import com.google.common.collect.Table;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.itis.scheduleplatform.generator.GeneticGenerator;
import ru.itis.scheduleplatform.generator.RandomScheduleGenerator;
import ru.itis.scheduleplatform.generator.SimulatedAnnealingGenerator;
import ru.itis.scheduleplatform.io.XlsxWriter;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.Group;
import ru.itis.scheduleplatform.models.ScheduleCell;
import ru.itis.scheduleplatform.models.genetic.Schedule;

@SpringBootApplication
public class SchedulePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulePlatformApplication.class, args);
    }

}
