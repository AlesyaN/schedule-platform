package ru.itis.scheduleplatform.services.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.enums.ClassType;
import ru.itis.scheduleplatform.enums.DayOfWeek;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.*;
import ru.itis.scheduleplatform.models.genetic.Schedule;

import java.util.List;
import java.util.Random;

@Slf4j
@Service("freeDayHandler")
public class FeeDayHandler extends Handler {

    @Override
    public void handleRequest(GeneratorParameters generatorParameters, List<ScheduleParameters> scheduleParametersList) {
        log.debug("Free day handler processing");
        if (generatorParameters.isFreeDayRequired()) {
            Random random = new Random();
            Class freeClass = Class.builder().classType(ClassType.SEMINAR).subject(new Subject("Библиотечный день")).build();
            for (ScheduleParameters scheduleParameters : scheduleParametersList) {
                for (Group group : scheduleParameters.getGroups()) {
                    DayOfWeek freeDay = DayOfWeek.values()[random.nextInt(DayOfWeek.values().length)];
                    for (TimeSlot timeSlot : scheduleParameters.getTimeSlots()) {
                        scheduleParameters.getSchedule().put(ScheduleCell.builder().dayOfWeek(freeDay).timeSlot(timeSlot).build(),
                                group, freeClass);
                    }
                }
            }
        }
        next.handleRequest(generatorParameters, scheduleParametersList);
    }
}
