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
        ApplicationContext context = SpringApplication.run(SchedulePlatformApplication.class, args);
//        RandomScheduleGenerator generator = context.getBean(RandomScheduleGenerator.class);
//        XlsxWriter writer = context.getBean(XlsxWriter.class);
//        GeneticGenerator genetic = context.getBean(GeneticGenerator.class);
//        SimulatedAnnealingGenerator simulatedAnnealingGenerator = context.getBean(SimulatedAnnealingGenerator.class);
//
//        Table<ScheduleCell, Group, Class> schedule = simulatedAnnealingGenerator.generate("name").getSchedule();
//        writer.exportScheduleToFile(schedule, "src/main/resources/timetable.xlsx");
    }

}
