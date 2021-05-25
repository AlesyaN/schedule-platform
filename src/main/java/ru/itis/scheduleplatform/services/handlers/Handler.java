package ru.itis.scheduleplatform.services.handlers;

import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.models.genetic.Schedule;

import java.util.List;

public abstract class Handler {

    protected Handler next;

    public abstract void handleRequest(GeneratorParameters generatorParameters, List<ScheduleParameters> scheduleParameters);

    public void setNext(Handler next) {
        this.next = next;
    }
}
