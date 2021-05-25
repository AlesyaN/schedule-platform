package ru.itis.scheduleplatform.models;

import lombok.Builder;
import lombok.Data;
import ru.itis.scheduleplatform.enums.DayOfWeek;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name = "schedule_cell")
public class ScheduleCell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "time_slot_id")
    private TimeSlot timeSlot;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Override
    public String toString() {
        return "ScheduleCell{" +
                "timeSlot=" + timeSlot +
                ", dayOfWeek=" + dayOfWeek +
                '}';
    }
}
