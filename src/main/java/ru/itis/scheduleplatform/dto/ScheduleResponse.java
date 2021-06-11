package ru.itis.scheduleplatform.dto;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.Group;
import ru.itis.scheduleplatform.models.ScheduleCell;
import ru.itis.scheduleplatform.models.genetic.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {
    @Id
    @Field("_id")
    private ObjectId objectId;

    private String id;

    private String name;
    private UUID populationId;
    private Integer fitness;
    private List<ScheduleItem> schedule;
    private ScheduleParameters scheduleParameters;

    public static ScheduleResponse fromSchedule(Schedule schedule) {
        ScheduleResponse response =  ScheduleResponse.builder()
                .fitness(schedule.getFitness())
                .schedule(mapSchedule(schedule))
                .name(schedule.getScheduleParameters().getName())
                .populationId(schedule.getPopulationId())
                .scheduleParameters(schedule.getScheduleParameters())
                .build();
        if (schedule.getId() != null) {
            response.setObjectId(new ObjectId(schedule.getId()));
            response.setId(schedule.getId());
        }
        return response;
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
    public static class ScheduleItem {
        private Group group;
        private List<CellClass> classes;

        @AllArgsConstructor
        @Getter
        public static class CellClass {
            private ScheduleCell scheduleCell;
            private Class lesson;
        }
    }
}
