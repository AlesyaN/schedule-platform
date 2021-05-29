package ru.itis.scheduleplatform.generator;


import com.google.common.collect.Table;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.Group;
import ru.itis.scheduleplatform.models.ScheduleCell;

public interface Generator {
    Table<ScheduleCell, Group, Class> generate(ScheduleParameters scheduleParameters);
}
