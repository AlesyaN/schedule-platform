package ru.itis.scheduleplatform.dto;

import com.google.common.collect.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.Group;
import ru.itis.scheduleplatform.models.ScheduleCell;
import ru.itis.scheduleplatform.models.TimeSlot;

import java.util.List;

@Setter
@Getter
@Builder
public class ScheduleParameters {
    private List<Group> groups;
    private List<TimeSlot> timeSlots;
    private Table<ScheduleCell, Group, Class> schedule;

}
