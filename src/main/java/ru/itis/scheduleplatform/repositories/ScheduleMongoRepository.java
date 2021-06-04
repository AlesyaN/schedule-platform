package ru.itis.scheduleplatform.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.itis.scheduleplatform.dto.ScheduleResponse;

import java.util.List;
import java.util.UUID;

public interface ScheduleMongoRepository extends MongoRepository<ScheduleResponse, String> {

    List<ScheduleResponse> findAllByPopulationId(UUID populationId);
}
