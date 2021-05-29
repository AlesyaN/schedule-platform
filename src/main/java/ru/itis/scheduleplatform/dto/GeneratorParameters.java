package ru.itis.scheduleplatform.dto;

import lombok.Data;
import ru.itis.scheduleplatform.enums.DayOfWeek;

@Data
public class GeneratorParameters {
    private String name;

    private Integer numberOfLectureDays;

    private FixAuditoriumFor fixAuditoriumFor;

    private Boolean isFreeDayRequired;

    private Boolean divideOnShifts;

    private int semesterNumber;

    private AlgorithmType algorithmType;

    public enum FixAuditoriumFor {
        GROUP,
        SUBJECT,
        TEACHER
    }

    public enum AlgorithmType {
        GENETIC,
        ANNEALING
    }

    @Override
    public String toString() {
        return "{ name='" + name + '\'' +
                ", numberOfLectureDays=" + numberOfLectureDays +
                ", fixAuditoriumFor=" + fixAuditoriumFor +
                ", isFreeDayRequired=" + isFreeDayRequired +
                ", divideOnShifts=" + divideOnShifts +
                ", semesterNumber=" + semesterNumber +
                '}';
    }
}
