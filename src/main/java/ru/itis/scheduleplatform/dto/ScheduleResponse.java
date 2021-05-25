package ru.itis.scheduleplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.val;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.Group;
import ru.itis.scheduleplatform.models.ScheduleCell;
import ru.itis.scheduleplatform.models.genetic.Schedule;

import java.util.*;

@Builder
@Getter
public class ScheduleResponse {
    private String name;
    private Integer fitness;
    private List<ScheduleItem> schedule;

    public static ScheduleResponse fromSchedule(Schedule schedule) {
        return ScheduleResponse.builder()
                .fitness(schedule.getFitness())
                .schedule(mapSchedule(schedule))
                .name(schedule.getName())
                .build();
    }

    private static List<ScheduleItem> mapSchedule(Schedule schedule) {
        List<ScheduleItem> result = new ArrayList<>();
        val scheduleItems = schedule.getSchedule().columnMap().entrySet();
        val iterator = scheduleItems.iterator();
        while (iterator.hasNext()) {
            val entry = iterator.next();
            Group group = entry.getKey();
            ScheduleItem scheduleItem = new ScheduleItem(group, new ArrayList<>());
            entry.getValue().forEach((key, value) -> scheduleItem.classes.add(new ScheduleItem.CellClass(key, value)));
            result.add(scheduleItem);
        }
        return result;
    }

    @AllArgsConstructor
    @Getter
    private static class ScheduleItem {
        private Group group;
        private List<CellClass> classes;

        @AllArgsConstructor
        @Getter
        private static class CellClass {
            private ScheduleCell scheduleCell;
            private Class lesson;
        }
    }
}
