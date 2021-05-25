package ru.itis.scheduleplatform.models;

import lombok.Data;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "time_slot")
public class TimeSlot implements Comparable<TimeSlot> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;
    private LocalTime start;

    @Column(name = "\"end\"")
    private LocalTime end;

    @Override
    public String toString() {
        return start + "-" + end;
    }

    @Override
    public int compareTo(TimeSlot timeSlot) {
        return this.number - timeSlot.number;
    }
}
