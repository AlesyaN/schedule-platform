package ru.itis.scheduleplatform.services;

import com.google.common.collect.HashBasedTable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.dto.ScheduleResponse;
import ru.itis.scheduleplatform.generator.GeneticGenerator;
import ru.itis.scheduleplatform.io.XlsxWriter;
import ru.itis.scheduleplatform.models.genetic.Schedule;
import ru.itis.scheduleplatform.repositories.GroupRepository;
import ru.itis.scheduleplatform.repositories.TimeSlotRepository;
import ru.itis.scheduleplatform.services.handlers.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DebugService {

    private Handler shiftsHandler;
    private GroupRepository groupRepository;
    private TimeSlotRepository timeSlotRepository;
    private XlsxWriter xlsxWriter;
    private GeneticGenerator geneticGenerator;

    public DebugService(@Qualifier("shiftsHandler") Handler shiftsHandler,
                        @Qualifier("freeDayHandler") Handler freeDayHandler,
                        @Qualifier("debugHandler") Handler debugHandler,
                        @Qualifier("divideByClassTypeHandler") Handler divideByClassTypeHandler,
                        @Qualifier("fixAuditoriumHandler") Handler fixAuditoriumHandler,
                        GroupRepository groupRepository,
                        TimeSlotRepository timeSlotRepository, XlsxWriter xlsxWriter,
                        GeneticGenerator geneticGenerator) {
        this.groupRepository = groupRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.xlsxWriter = xlsxWriter;
        shiftsHandler.setNext(freeDayHandler);
        freeDayHandler.setNext(divideByClassTypeHandler);
        divideByClassTypeHandler.setNext(fixAuditoriumHandler);
        fixAuditoriumHandler.setNext(debugHandler);

        this.shiftsHandler = shiftsHandler;
        this.geneticGenerator = geneticGenerator;
    }

    public List<ScheduleResponse> initGeneration(GeneratorParameters parameters) {
        ScheduleParameters scheduleParameters = ScheduleParameters.builder()
                .name(parameters.getName())
                .scheduleTable(HashBasedTable.create())
                .groups(groupRepository.findAll())
                .timeSlots(timeSlotRepository.findAll())
                .build();
        List<ScheduleParameters> scheduleParametersList = new ArrayList<>(List.of(scheduleParameters));

        List<Schedule> schedule = shiftsHandler.handleRequest(parameters, scheduleParametersList);
        List<ScheduleResponse> scheduleResponseList = new ArrayList<>();
        for (Schedule s : schedule) {
            scheduleResponseList.add(ScheduleResponse.fromSchedule(s));
        }
        return scheduleResponseList;
    }

    public List<ScheduleResponse> nextIteration(List<UUID> populationIdList) {
        List<ScheduleResponse> responses = new ArrayList<>();
        for (UUID id : populationIdList) {
            responses.add(ScheduleResponse.fromSchedule(geneticGenerator.processNextIteration(id)));
        }
        return responses;
    }
}
