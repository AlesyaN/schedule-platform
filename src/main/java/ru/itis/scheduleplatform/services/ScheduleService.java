package ru.itis.scheduleplatform.services;

import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.SaveParameters;
import ru.itis.scheduleplatform.dto.ScheduleResponse;
import ru.itis.scheduleplatform.dto.ScheduleResponseExtended;
import ru.itis.scheduleplatform.models.ScheduleIds;
import ru.itis.scheduleplatform.repositories.ScheduleIdsRepository;
import ru.itis.scheduleplatform.repositories.ScheduleMongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ScheduleService {

    private ScheduleIdsRepository scheduleIdsRepository;
    private ScheduleMongoRepository scheduleMongoRepository;

    public ScheduleService(ScheduleIdsRepository scheduleIdsRepository, ScheduleMongoRepository scheduleMongoRepository) {
        this.scheduleIdsRepository = scheduleIdsRepository;
        this.scheduleMongoRepository = scheduleMongoRepository;
    }

    public List<ScheduleResponseExtended> getSchedules() {
        List<ScheduleIds> scheduleIds = scheduleIdsRepository.findAll();
        List<ScheduleResponseExtended> scheduleResponseList = new ArrayList<>();
        for (ScheduleIds scheduleId : scheduleIds) {
            ScheduleResponse scheduleResponse = scheduleMongoRepository
                    .getById(new ObjectId(scheduleId.getBestScheduleId()))
                    .orElseThrow(IllegalArgumentException::new);
            ScheduleResponseExtended scheduleResponseExtended = new ScheduleResponseExtended();
            BeanUtils.copyProperties(scheduleResponse, scheduleResponseExtended);
            scheduleResponseExtended.setIterationNumber(scheduleId.getIterationNumber());
            scheduleResponseExtended.setId(scheduleId.getBestScheduleId());
            scheduleResponseList.add(scheduleResponseExtended);
        }
        return scheduleResponseList;
    }

    public ScheduleIds save(SaveParameters saveParameters) {
        return scheduleIdsRepository.save(new ScheduleIds(saveParameters.getId(), UUID.fromString(saveParameters.getPopulationId()), saveParameters.getIterationNumber()));
    }

    public ScheduleResponse getSchedule(String id) {
        return scheduleMongoRepository.getById(new ObjectId(id)).orElseThrow(IllegalArgumentException::new);
    }

}
