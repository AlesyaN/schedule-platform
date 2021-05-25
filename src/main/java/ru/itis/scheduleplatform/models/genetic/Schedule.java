package ru.itis.scheduleplatform.models.genetic;

import com.google.common.collect.Table;
import lombok.Data;
import ru.itis.scheduleplatform.enums.DayOfWeek;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.Group;
import ru.itis.scheduleplatform.models.ScheduleCell;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class Schedule {
    private Table<ScheduleCell, Group, Class> schedule;
    private Integer fitness;
    private String name;

    public static final int MAX_FITNESS = 200;

    public Schedule(Table<ScheduleCell, Group, Class> schedule) {
        this.schedule = schedule;
        this.fitness = countFitness();
    }

    private Integer countFitness() {
        int fitness = MAX_FITNESS;

        //количество окон между парами
        fitness -= getCountOfOpenings();

        //разница в количестве пар в один день
        fitness -= getDifferenceBetweenClassesCountPerDay();

        return fitness;
    }

    private int getDifferenceBetweenClassesCountPerDay() {
        int globalDifference = 0;
        Set<Group> groups = schedule.columnKeySet();
        for (Group group : groups) {
            List<ScheduleCell> scheduleCells = new ArrayList<>(schedule.column(group).keySet());
            int minCount = (int) scheduleCells.stream().filter(cell -> cell.getDayOfWeek().equals(DayOfWeek.MONDAY)).count();
            int maxCount = minCount;
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                int countForDay = (int) scheduleCells.stream().filter(cell -> cell.getDayOfWeek().equals(dayOfWeek)).count();
                if (countForDay < minCount)
                    minCount = countForDay;
                if (countForDay > maxCount)
                    maxCount = countForDay;
            }
            globalDifference += maxCount - minCount;
        }
        return globalDifference;
    }

    private int getCountOfOpenings() {
        Set<Group> groups = schedule.columnKeySet();
        int countOfOpenings = 0;
        for (Group group : groups) {
            List<ScheduleCell> scheduleCells = new ArrayList<>(schedule.column(group).keySet());
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                List<ScheduleCell> scheduleForDay = scheduleCells.stream().filter(cell -> cell.getDayOfWeek().equals(dayOfWeek)).collect(Collectors.toList());
                if (scheduleForDay.size() == 0)
                    continue;
                ScheduleCell min = scheduleForDay.get(0);
                ScheduleCell max = scheduleForDay.get(0);
                for (ScheduleCell cell : scheduleForDay) {
                    if (cell.getTimeSlot().getStart().isBefore(min.getTimeSlot().getStart()))
                        min = cell;
                    if (cell.getTimeSlot().getStart().isAfter(max.getTimeSlot().getStart()))
                        max = cell;
                }
                int classesCountWithOpenings = max.getTimeSlot().getNumber() - min.getTimeSlot().getNumber() + 1;
                int classesCount = scheduleForDay.size();
                countOfOpenings += classesCountWithOpenings - classesCount;
            }
        }
        return countOfOpenings;
    }
}
