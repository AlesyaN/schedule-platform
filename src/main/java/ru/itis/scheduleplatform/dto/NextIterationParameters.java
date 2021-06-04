package ru.itis.scheduleplatform.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class NextIterationParameters {
    private List<UUID> populationIdList;
}
