package ru.itis.scheduleplatform.dto;

import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import ru.itis.scheduleplatform.enums.DayOfWeek;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.*;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class ScheduleParameters {
    private List<Group> groups;
    private List<TimeSlot> timeSlots;
    @Transient
    private Table<ScheduleCell, Group, Class> scheduleTable;
    private List<DayOfWeek> lectureDays;

    @Transient
    private Map<Group, Auditorium> groupAuditoriumMap;

    @PersistenceConstructor
    public ScheduleParameters(List<Group> groups, List<TimeSlot> timeSlots, List<DayOfWeek> lectureDays) {
        this.groups = groups;
        this.timeSlots = timeSlots;
        this.lectureDays = lectureDays;
    }
}
