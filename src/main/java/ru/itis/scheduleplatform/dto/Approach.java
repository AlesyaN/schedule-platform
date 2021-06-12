package ru.itis.scheduleplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class Approach {

    private Integer numberOfLectureDays;

    private GeneratorParameters.FixAuditoriumFor fixAuditoriumFor;

    private Boolean isFreeDayRequired;

    private Boolean divideOnShifts;

    private GeneratorParameters.AlgorithmType algorithmType;

    public static Approach from(GeneratorParameters generatorParameters) {
        return Approach.builder()
                .numberOfLectureDays(generatorParameters.getNumberOfLectureDays())
                .fixAuditoriumFor(generatorParameters.getFixAuditoriumFor())
                .isFreeDayRequired(generatorParameters.getIsFreeDayRequired())
                .divideOnShifts(generatorParameters.getDivideOnShifts())
                .algorithmType(generatorParameters.getAlgorithmType())
                .build();
    }

}
