package ru.itis.scheduleplatform.services.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.constants.Const;
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
    public Schedule handleRequest(GeneratorParameters generatorParameters, List<ScheduleParameters> scheduleParametersList) {
        if (generatorParameters.getIsFreeDayRequired()) {
            log.debug("Free day handler processing");
            Random random = new Random();
            Class freeClass = Class.builder().classType(ClassType.SEMINAR).subject(new Subject(Const.FREE_DAY_SUBJECT_NAME)).build();
            for (ScheduleParameters scheduleParameters : scheduleParametersList) {
                DayOfWeek freeDay = DayOfWeek.values()[random.nextInt(DayOfWeek.values().length)];
                for (Group group : scheduleParameters.getGroups()) {
                    for (TimeSlot timeSlot : scheduleParameters.getTimeSlots()) {
                        scheduleParameters.getSchedule().put(ScheduleCell.builder().dayOfWeek(freeDay).timeSlot(timeSlot).build(),
                                group, freeClass);
                    }
                }
            }
        }
        return next.handleRequest(generatorParameters, scheduleParametersList);
    }
}
