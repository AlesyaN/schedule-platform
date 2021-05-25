package ru.itis.scheduleplatform.generator;


import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.models.genetic.Schedule;

import java.util.List;

public interface Generator {
    Schedule generate(List<ScheduleParameters> scheduleParameters);
}
