package ru.itis.scheduleplatform.models.genetic;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.dto.ScheduleResponse;
import ru.itis.scheduleplatform.enums.DayOfWeek;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.Group;
import ru.itis.scheduleplatform.models.ScheduleCell;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private UUID populationId;
    private Table<ScheduleCell, Group, Class> schedule;
    private Integer fitness;
    private String name;
    private ScheduleParameters scheduleParameters;

    public static final int MAX_FITNESS = 200;

    public Schedule(Table<ScheduleCell, Group, Class> schedule) {
        this.schedule = schedule;
        this.fitness = countFitness();
    }

    public Schedule(Table<ScheduleCell, Group, Class> schedule, UUID populationId) {
        this.schedule = schedule;
        this.fitness = countFitness();
        this.populationId = populationId;
    }

    public Schedule(Table<ScheduleCell, Group, Class> scheduleTable, UUID populationId, ScheduleParameters scheduleParameters) {
        this.schedule = scheduleTable;
        this.fitness = countFitness();
        this.populationId = populationId;
        this.scheduleParameters = scheduleParameters;
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

    public static List<Schedule> convert(List<ScheduleResponse> scheduleResponseList) {
        List<Schedule> result = new ArrayList<>();
        for (ScheduleResponse scheduleResponse : scheduleResponseList) {
            result.add(Schedule.from(scheduleResponse));
        }
        return result;
    }

    public static Schedule from(ScheduleResponse scheduleResponse) {
        Table<ScheduleCell, Group, Class> schedule = HashBasedTable.create();
        for (ScheduleResponse.ScheduleItem item : scheduleResponse.getSchedule()) {
            for (ScheduleResponse.ScheduleItem.CellClass cellClass : item.getClasses()) {
                schedule.put(cellClass.getScheduleCell(), item.getGroup(), cellClass.getLesson());
            }
        }
        return Schedule.builder()
                .schedule(schedule)
                .fitness(scheduleResponse.getFitness())
                .name(scheduleResponse.getName())
                .populationId(scheduleResponse.getPopulationId())
                .scheduleParameters(scheduleResponse.getScheduleParameters())
                .build();
    }
}
