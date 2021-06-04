package ru.itis.scheduleplatform.generator;


import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.models.genetic.Schedule;

public interface Generator {
    Schedule generate(ScheduleParameters scheduleParameters);
}
