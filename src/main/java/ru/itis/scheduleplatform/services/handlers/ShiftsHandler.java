package ru.itis.scheduleplatform.services.handlers;

import com.google.common.collect.HashBasedTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.models.Group;
import ru.itis.scheduleplatform.models.TimeSlot;
import ru.itis.scheduleplatform.models.genetic.Schedule;
import ru.itis.scheduleplatform.repositories.GroupRepository;
import ru.itis.scheduleplatform.repositories.TimeSlotRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("shiftsHandler")
public class ShiftsHandler extends Handler {

    private GroupRepository groupRepository;
    private TimeSlotRepository timeSlotRepository;

    public ShiftsHandler(GroupRepository groupRepository, TimeSlotRepository timeSlotRepository) {
        this.groupRepository = groupRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    public Schedule handleRequest(GeneratorParameters generatorParameters, List<ScheduleParameters> scheduleParameters) {
        log.debug("Shifts handler processing");
        if (generatorParameters.getDivideOnShifts()) {
            List<Group> allGroups = groupRepository.findAllBySemesterNumber(generatorParameters.getSemesterNumber())
                    .stream().sorted().collect(Collectors.toList());
            List<Group> firstShiftGroups = allGroups.subList(0, allGroups.size() / 2);
            List<Group> secondShiftGroups = allGroups.subList(allGroups.size() / 2, allGroups.size());

            List<TimeSlot> timeSlots = timeSlotRepository.findAll().stream().sorted().collect(Collectors.toList());
            List<TimeSlot> firstShiftTimeSlots = timeSlots.subList(0, timeSlots.size() / 2);
            List<TimeSlot> secondShiftTimeSlots = timeSlots.subList(timeSlots.size() / 2, timeSlots.size());

            scheduleParameters.get(0).setGroups(firstShiftGroups);
            scheduleParameters.get(0).setTimeSlots(firstShiftTimeSlots);

            ScheduleParameters sp = ScheduleParameters.builder()
                    .groups(secondShiftGroups)
                    .timeSlots(secondShiftTimeSlots)
                    .schedule(HashBasedTable.create()).build();

            scheduleParameters.add(sp);
        }
        return next.handleRequest(generatorParameters, scheduleParameters);
    }
}
