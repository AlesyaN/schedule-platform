package ru.itis.scheduleplatform.services;

import com.google.common.collect.HashBasedTable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.dto.ScheduleResponse;
import ru.itis.scheduleplatform.repositories.GroupRepository;
import ru.itis.scheduleplatform.repositories.TimeSlotRepository;
import ru.itis.scheduleplatform.services.handlers.Handler;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenerationService {

    private Handler shiftsHandler;
    private Handler freeDayHandler;
    private Handler algorithmHandler;
    private GroupRepository groupRepository;
    private TimeSlotRepository timeSlotRepository;

    public GenerationService(@Qualifier("shiftsHandler") Handler shiftsHandler,
                             @Qualifier("freeDayHandler") Handler freeDayHandler,
                             @Qualifier("algorithmHandler") Handler algorithmHandler,
                             GroupRepository groupRepository,
                             TimeSlotRepository timeSlotRepository) {
        this.groupRepository = groupRepository;
        this.timeSlotRepository = timeSlotRepository;
        shiftsHandler.setNext(freeDayHandler);
        freeDayHandler.setNext(algorithmHandler);

        this.shiftsHandler = shiftsHandler;
        this.freeDayHandler = freeDayHandler;
        this.algorithmHandler = algorithmHandler;
    }

    public ScheduleResponse process(GeneratorParameters parameters) {
        ScheduleParameters scheduleParameters = ScheduleParameters.builder()
                .schedule(HashBasedTable.create())
                .groups(groupRepository.findAll())
                .timeSlots(timeSlotRepository.findAll())
                .build();
        List<ScheduleParameters> scheduleParametersList = new ArrayList<>(List.of(scheduleParameters));

//        return ScheduleResponse.fromSchedule(shiftsHandler.handleRequest(parameters, scheduleParametersList));
        shiftsHandler.handleRequest(parameters, scheduleParametersList);
        return null;
    }

}
