package ru.itis.scheduleplatform.dto;

import com.google.common.collect.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.itis.scheduleplatform.enums.DayOfWeek;
import ru.itis.scheduleplatform.models.*;
import ru.itis.scheduleplatform.models.Class;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@Builder
public class ScheduleParameters {
    private List<Group> groups;
    private List<TimeSlot> timeSlots;
    private Table<ScheduleCell, Group, Class> schedule;
    private Integer maxGroupCountInParallel;
    private List<DayOfWeek> lectureDays;
    private Map<Group, Auditorium> groupAuditoriumMap;
}
