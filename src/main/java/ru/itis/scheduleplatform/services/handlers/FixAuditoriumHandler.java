package ru.itis.scheduleplatform.services.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.enums.ClassType;
import ru.itis.scheduleplatform.models.Auditorium;
import ru.itis.scheduleplatform.models.Group;
import ru.itis.scheduleplatform.models.genetic.Schedule;
import ru.itis.scheduleplatform.repositories.AuditoriumRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Slf4j
@Service("fixAuditoriumHandler")
public class FixAuditoriumHandler extends Handler {

    private AuditoriumRepository auditoriumRepository;

    public FixAuditoriumHandler(AuditoriumRepository auditoriumRepository) {
        this.auditoriumRepository = auditoriumRepository;
    }

    @Override
    public Schedule handleRequest(GeneratorParameters generatorParameters, List<ScheduleParameters> scheduleParameters) {
        Random random = new Random();
        List<Auditorium> auditoriums = auditoriumRepository.findAllByAllowedClassTypesIn(List.of(ClassType.SEMINAR));
        switch (generatorParameters.getFixAuditoriumFor()) {
            case GROUP:
                for (ScheduleParameters sp : scheduleParameters) {
                    sp.setGroupAuditoriumMap(new HashMap<>());
                    for (Group group : sp.getGroups()) {
                        Auditorium auditorium;
                        do {
                            auditorium = auditoriums.get(random.nextInt(auditoriums.size()));
                        } while (sp.getGroupAuditoriumMap().containsValue(auditorium));
                        sp.getGroupAuditoriumMap().put(group, auditorium);
                    }
                }
            default:
                return next.handleRequest(generatorParameters, scheduleParameters);
        }
    }
}
