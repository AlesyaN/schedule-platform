package ru.itis.scheduleplatform.services.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.enums.DayOfWeek;
import ru.itis.scheduleplatform.models.genetic.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service("divideByClassTypeHandler")
public class DivideByClassTypeHandler extends Handler {
    @Override
    public List<Schedule> handleRequest(GeneratorParameters generatorParameters, List<ScheduleParameters> scheduleParameters) {
        if (generatorParameters.getNumberOfLectureDays() != null) {
            Random random = new Random();
            for (ScheduleParameters sp : scheduleParameters) {
                sp.setLectureDays(new ArrayList<>());
                for (int i = 0; i < generatorParameters.getNumberOfLectureDays(); i++) {
                    DayOfWeek lectureDayOfWeek = null;
                    do {
                        lectureDayOfWeek = DayOfWeek.values()[random.nextInt(DayOfWeek.values().length)];
                    } while (sp.getLectureDays().contains(lectureDayOfWeek));
                    sp.getLectureDays().add(lectureDayOfWeek);
                }
            }
        }
        return next.handleRequest(generatorParameters, scheduleParameters);
    }
}
