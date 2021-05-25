package ru.itis.scheduleplatform.models.genetic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParentsPair {
    private Schedule parent1;
    private Schedule parent2;
}
