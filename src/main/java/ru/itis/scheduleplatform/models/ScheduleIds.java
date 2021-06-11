package ru.itis.scheduleplatform.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleIds {

    @Id
    private String bestScheduleId;

    private UUID populationId;

    private Integer iterationNumber;

}
