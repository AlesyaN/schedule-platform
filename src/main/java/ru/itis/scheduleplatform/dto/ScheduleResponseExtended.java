package ru.itis.scheduleplatform.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class ScheduleResponseExtended extends ScheduleResponse {

    private Number iterationNumber;

    ScheduleResponseExtended(ObjectId objectId, String id, String name, UUID populationId, Integer fitness, List<ScheduleItem> schedule, ScheduleParameters scheduleParameters) {
        super(objectId, id, name, populationId, fitness, schedule, scheduleParameters);
    }
}
