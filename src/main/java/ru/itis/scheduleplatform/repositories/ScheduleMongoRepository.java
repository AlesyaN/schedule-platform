package ru.itis.scheduleplatform.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.itis.scheduleplatform.dto.ScheduleResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleMongoRepository extends MongoRepository<ScheduleResponse, ObjectId> {

    List<ScheduleResponse> findAllByPopulationId(UUID populationId);

    List<ScheduleResponse> findAllByIdIsIn(List<String> ids);

    @Query("{ '_id' : ?0 }")
    Optional<ScheduleResponse> getById(ObjectId id);
}
