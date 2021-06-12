package ru.itis.scheduleplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class InputDataSize {
    private int auditoriums;
    private int groups;
    private int teachers;
}
