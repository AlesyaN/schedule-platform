package ru.itis.scheduleplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itis.scheduleplatform.models.Auditorium;
import ru.itis.scheduleplatform.models.Group;
import ru.itis.scheduleplatform.models.Teacher;

import java.util.List;
import java.util.Map;

@Data
public class GeneratorParameters {
    private String name;

    private Integer numberOfLectureDays;

    private FixAuditoriumFor fixAuditoriumFor;

    private Boolean isFreeDayRequired;

    private Boolean divideOnShifts;

    private int semesterNumber;

    private AlgorithmType algorithmType;

    private InputData inputData;

    public enum FixAuditoriumFor {
        GROUP,
        SUBJECT,
        TEACHER
    }

    public enum AlgorithmType {
        GENETIC,
        ANNEALING
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InputData {
        private List<AuditoriumDto> auditoriums;
        private List<Group> groups;
        private List<TeacherDto> teachers;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class AuditoriumDto {
            private String roomNumber;
            private Integer capacity;
            private List<String> allowedClassTypes;
        }

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class TeacherDto {
            private String name;
            private String surname;
            private String patronymic;
            private List<SubjectClassType> subjects;

            @Getter
            @AllArgsConstructor
            @NoArgsConstructor
            public static class SubjectClassType {
                String subject;
                List<String> classTypes;
            }
        }
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
