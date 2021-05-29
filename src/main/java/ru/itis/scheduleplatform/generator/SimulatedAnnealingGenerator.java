package ru.itis.scheduleplatform.generator;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.constants.Const;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.enums.DayOfWeek;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.Group;
import ru.itis.scheduleplatform.models.ScheduleCell;
import ru.itis.scheduleplatform.models.TimeSlot;
import ru.itis.scheduleplatform.models.genetic.Schedule;

import java.util.ArrayList;
import java.util.Random;

@Slf4j
@Service
public class SimulatedAnnealingGenerator implements Generator {
    private static final int SEMESTER_NUMBER = 1;
    private static final double T_MAX = 1000;

    private RandomScheduleGenerator randomScheduleGenerator;
    private ScheduleParameters scheduleParameters;

    public SimulatedAnnealingGenerator(RandomScheduleGenerator randomScheduleGenerator) {
        this.randomScheduleGenerator = randomScheduleGenerator;
    }

    @Override
    public Table<ScheduleCell, Group, Class> generate(ScheduleParameters scheduleParameters) {
        this.scheduleParameters = scheduleParameters;
        log.debug("Generation using simulated annealing algorithm");
        Table<ScheduleCell, Group, Class> scheduleTable = randomScheduleGenerator.generate(scheduleParameters);
        Schedule currentSchedule = new Schedule(scheduleTable);
        int currentFitness = currentSchedule.getFitness();
        double temperature = T_MAX;
        for (int i = 0; i < 1000; i++) {
            log.debug("Generating candidate number " + i);
            val scheduleCandidate = generateScheduleCandidate(currentSchedule);
            val candidateFitness = scheduleCandidate.getFitness();
            if (candidateFitness > currentFitness) {
                currentSchedule = scheduleCandidate;
                currentFitness = currentSchedule.getFitness();
            } else {
                val p = calculateTransitionProbability(candidateFitness - currentFitness, temperature);
                if (isMakeTransition(p)) {
                    currentSchedule = scheduleCandidate;
                    currentFitness = currentSchedule.getFitness();
                }
            }
            temperature = decreaseTemperature(i);
        }

        return currentSchedule.getSchedule();
    }

    private double decreaseTemperature(int i) {
        return T_MAX * 0.1 / i;
    }

    private boolean isMakeTransition(double probability) {
        Random r = new Random();
        return r.nextInt(1) <= probability;
    }

    private double calculateTransitionProbability(int fitnessDelta, double temperature) {
        return Math.exp(-fitnessDelta / temperature);
    }

    private Schedule generateScheduleCandidate(Schedule currentSchedule) {
        val cell1 = getRandomScheduleCell();
        val cell2 = getRandomScheduleCell();
        return invertScheduleBetweenTwoCells(currentSchedule, cell1, cell2);
    }

    private ScheduleCell getRandomScheduleCell() {
        Random random = new Random();
        val daysOfWeek = DayOfWeek.values();
        val dayOfWeek = daysOfWeek[random.nextInt(daysOfWeek.length)];
        val timeSlots = scheduleParameters.getTimeSlots();
        val timeSlot = timeSlots.get(random.nextInt(timeSlots.size()));
        return ScheduleCell.builder().dayOfWeek(dayOfWeek).timeSlot(timeSlot).build();
    }

    private Schedule invertScheduleBetweenTwoCells(Schedule schedule, ScheduleCell cell1, ScheduleCell cell2) {
        Table<ScheduleCell, Group, Class> invertedSchedule = HashBasedTable.create();
        val scheduleTable = schedule.getSchedule();

        // составляем список всех возможных ScheduleCells
        val scheduleCells = new ArrayList<ScheduleCell>();
        val timeSlots = scheduleParameters.getTimeSlots();
        Long id = 0L;
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            for (TimeSlot timeSlot : timeSlots) {
                scheduleCells.add(ScheduleCell.builder().id(id).dayOfWeek(dayOfWeek).timeSlot(timeSlot).build());
                id++;
            }
        }

        //меням местами i1 и i2, если i1 > i2
        int i1 = scheduleCells.indexOf(cell1);
        int i2 = scheduleCells.indexOf(cell2);

        if (i1 == i2) {
            return schedule;
        } else if (i1 > i2) {
            val t = i1;
            i1 = i2;
            i2 = t;

            val tempCell = cell1;
            cell1 = cell2;
            cell2 = tempCell;
        }

        for (Group group : scheduleParameters.getGroups()) {
            // для каждой группы инвертируем занятия между i1 и i2
            ScheduleCell currentCell1 = cell1;
            ScheduleCell currentCell2 = cell2;
            for (int i = i1; i <= (i2 - i1) / 2 + i1; i++) {
                if (scheduleTable.get(currentCell1, group).getSubject().getName().equals(Const.FREE_DAY_SUBJECT_NAME) ||
                        scheduleTable.get(currentCell2, group).getSubject().getName().equals(Const.FREE_DAY_SUBJECT_NAME)) {
                    if (scheduleTable.get(currentCell2, group) != null) {
                        invertedSchedule.put(currentCell1, group, scheduleTable.get(currentCell2, group));
                    }
                    if (scheduleTable.get(currentCell1, group) != null) {
                        invertedSchedule.put(currentCell2, group, scheduleTable.get(currentCell1, group));
                    }
                    currentCell1 = scheduleCells.get(scheduleCells.indexOf(currentCell1) + 1);
                    currentCell2 = scheduleCells.get(scheduleCells.indexOf(currentCell2) - 1);
                }
            }
            // остальное копируем
            for (int i = 0; i < i1; i++) {
                val cell = scheduleCells.get(i);
                if (scheduleTable.get(cell, group) != null) {
                    invertedSchedule.put(cell, group, scheduleTable.get(cell, group));
                }
            }
            for (int i = i2 + 1; i < scheduleCells.size(); i++) {
                val cell = scheduleCells.get(i);
                if (scheduleTable.get(cell, group) != null) {
                    invertedSchedule.put(cell, group, scheduleTable.get(cell, group));
                }
            }
        }

        return new Schedule(invertedSchedule);
    }

}
