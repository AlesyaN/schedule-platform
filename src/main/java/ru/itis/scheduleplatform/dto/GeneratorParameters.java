package ru.itis.scheduleplatform.dto;

import lombok.Data;

@Data
public class GeneratorParameters {
    private String name;

    private boolean divideOnLectureAndPracticeDays;

    private FixAuditoriumFor fixAuditoriumFor;

    private boolean isFreeDayRequired;

    private boolean divideOnShifts;

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
                ", divideOnLectureAndPracticeDays=" + divideOnLectureAndPracticeDays +
                ", fixAuditoriumFor=" + fixAuditoriumFor +
                ", isFreeDayRequired=" + isFreeDayRequired +
                ", divideOnShifts=" + divideOnShifts +
                ", semesterNumber=" + semesterNumber +
                '}';
    }
}
