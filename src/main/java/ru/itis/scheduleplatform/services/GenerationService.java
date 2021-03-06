package ru.itis.scheduleplatform.services;

import com.google.common.collect.HashBasedTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.Approach;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.dto.ScheduleResponse;
import ru.itis.scheduleplatform.io.XlsxWriter;
import ru.itis.scheduleplatform.models.genetic.Schedule;
import ru.itis.scheduleplatform.repositories.GroupRepository;
import ru.itis.scheduleplatform.repositories.TimeSlotRepository;
import ru.itis.scheduleplatform.services.handlers.Handler;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GenerationService {

    private Handler shiftsHandler;
    private GroupRepository groupRepository;
    private TimeSlotRepository timeSlotRepository;
    private XlsxWriter xlsxWriter;

    public GenerationService(@Qualifier("shiftsHandler") Handler shiftsHandler,
                             @Qualifier("freeDayHandler") Handler freeDayHandler,
                             @Qualifier("algorithmHandler") Handler algorithmHandler,
                             @Qualifier("divideByClassTypeHandler") Handler divideByClassTypeHandler,
                             @Qualifier("fixAuditoriumHandler") Handler fixAuditoriumHandler,
                             GroupRepository groupRepository,
                             TimeSlotRepository timeSlotRepository, XlsxWriter xlsxWriter) {
        this.groupRepository = groupRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.xlsxWriter = xlsxWriter;
        shiftsHandler.setNext(freeDayHandler);
        freeDayHandler.setNext(divideByClassTypeHandler);
        divideByClassTypeHandler.setNext(fixAuditoriumHandler);
        fixAuditoriumHandler.setNext(algorithmHandler);

        this.shiftsHandler = shiftsHandler;
    }

    public List<ScheduleResponse> process(GeneratorParameters parameters) {
        ScheduleParameters scheduleParameters = ScheduleParameters.builder()
                .name(parameters.getName())
                .scheduleTable(HashBasedTable.create())
                .groups(groupRepository.findAll())
                .timeSlots(timeSlotRepository.findAll())
                .approach(Approach.from(parameters))
                .build();
        List<ScheduleParameters> scheduleParametersList = new ArrayList<>(List.of(scheduleParameters));

        List<Schedule> schedule = shiftsHandler.handleRequest(parameters, scheduleParametersList);
//        xlsxWriter.exportScheduleToFile(schedule.getSchedule(), "src/main/resources/timetable.xlsx");
        List<ScheduleResponse> scheduleResponseList = new ArrayList<>();
        for (Schedule s : schedule) {
            scheduleResponseList.add(ScheduleResponse.fromSchedule(s));
        }
        log.debug("Returned the response");
        return scheduleResponseList;
    }

}
