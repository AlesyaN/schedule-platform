package ru.itis.scheduleplatform.generator;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.dto.ScheduleResponse;
import ru.itis.scheduleplatform.enums.ClassType;
import ru.itis.scheduleplatform.enums.DayOfWeek;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.*;
import ru.itis.scheduleplatform.models.genetic.Schedule;
import ru.itis.scheduleplatform.repositories.AuditoriumRepository;
import ru.itis.scheduleplatform.repositories.ScheduleMongoRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GeneticGenerator implements Generator {

    private static final int POPULATION_COUNT = 20;
    private static final int MAX_FITNESS = 100;
    private static final int TOURNAMENT_COUNT = 20;
    private static final double PROBABILITY_OF_MUTATION = 3;
    private static final int SEMESTER_NUMBER = 1;
    private static final int MAX_SEMESTER_NUM = 2;

    private RandomScheduleGenerator randomScheduleGenerator;
    private AuditoriumRepository auditoriumRepository;
    private ScheduleParameters scheduleParameters;
    private ScheduleMongoRepository scheduleMongoRepository;

    public GeneticGenerator(RandomScheduleGenerator randomScheduleGenerator,
                            AuditoriumRepository auditoriumRepository,
                            ScheduleMongoRepository scheduleMongoRepository) {
        this.randomScheduleGenerator = randomScheduleGenerator;
        this.auditoriumRepository = auditoriumRepository;
        this.scheduleMongoRepository = scheduleMongoRepository;
    }

    public Schedule initGeneration(ScheduleParameters scheduleParameters) {
        this.scheduleParameters = scheduleParameters;
        List<Schedule> population = new ArrayList<>();
        UUID populationId = UUID.randomUUID();
        for (int i = 0; i < POPULATION_COUNT; i++) {
            Table<ScheduleCell, Group, Class> scheduleTable = randomScheduleGenerator.generate(scheduleParameters);
            Schedule s = new Schedule(scheduleTable, populationId, scheduleParameters);
            ScheduleResponse scheduleResponse = scheduleMongoRepository.save(ScheduleResponse.fromSchedule(s));
            s.setId(scheduleResponse.getObjectId().toString());
            population.add(s);
        }

        return getBestSchedule(population);
    }

    public Schedule processNextIteration(UUID populationId) {
        List<ScheduleResponse> scheduleResponseList = scheduleMongoRepository.findAllByPopulationId(populationId);
        List<Schedule> population = Schedule.convert(scheduleResponseList);
        this.scheduleParameters = population.get(0).getScheduleParameters();
        population = createNewPopulation(population);
        population.forEach(s -> {
            ScheduleResponse scheduleResponse = scheduleMongoRepository.save(ScheduleResponse.fromSchedule(s));
            s.setId(scheduleResponse.getObjectId().toString());
        });

        return getBestSchedule(population);
    }

    public Schedule generate(ScheduleParameters scheduleParameters) {
        this.scheduleParameters = scheduleParameters;
        List<Schedule> population = new ArrayList<>();
        UUID populationId = UUID.randomUUID();
        for (int i = 0; i < POPULATION_COUNT; i++) {
            Table<ScheduleCell, Group, Class> scheduleTable = randomScheduleGenerator.generate(scheduleParameters);
            population.add(new Schedule(scheduleTable, populationId));
        }
        calculatePopulationFitness(population);


        for (int i = 0; i < 100; i++) {
            population = createNewPopulation(population);
        }

        return getBestSchedule(population);
    }

    private Schedule getBestSchedule(List<Schedule> population) {

        return population.stream().max((scheduleSample1, scheduleSample2) -> {
            if (scheduleSample1.getFitness() > scheduleSample2.getFitness())
                return 1;
            else if (scheduleSample1.getFitness() < scheduleSample2.getFitness())
                return -1;
            return 0;
        }).orElseThrow(IllegalArgumentException::new);
    }

    private double calculatePopulationFitness(List<Schedule> population) {
        double fitness = new Double(population.stream().map(Schedule::getFitness).reduce(0, Integer::sum)) / population.size();
        log.info("Fitness of current popluation: " + fitness);
        return fitness;
    }

    private List<Schedule> createNewPopulation(List<Schedule> population) {
        List<Schedule> newPopulation = new ArrayList<>();
        UUID populationId = UUID.randomUUID();
        while (newPopulation.size() < POPULATION_COUNT) {
            Schedule[] parents = selectParentTournament(population);

            population.remove(parents[0]);
            population.remove(parents[1]);

            Schedule child1 = crossingOver(parents[0], parents[1]);
            Schedule child2 = crossingOver(parents[0], parents[1]);

            mutation(child1);
            mutation(child2);

            child1.setPopulationId(populationId);
            child2.setPopulationId(populationId);

            child1.setScheduleParameters(scheduleParameters);
            child2.setScheduleParameters(scheduleParameters);

            newPopulation.add(child1);
            newPopulation.add(child2);
        }

        return newPopulation;
    }


    private Schedule[] selectParentTournament(List<Schedule> population) {
        List<Schedule> tournamentPopulation = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < TOURNAMENT_COUNT; i++) {
            Schedule schedule = population.get(random.nextInt(population.size()));
            tournamentPopulation.add(schedule);
        }

        tournamentPopulation = tournamentPopulation.stream().sorted((scheduleSample1, scheduleSample2) -> {
            if (scheduleSample1.getFitness() > scheduleSample2.getFitness())
                return -1;
            else if (scheduleSample1.getFitness() < scheduleSample2.getFitness())
                return 1;
            return 0;
        }).collect(Collectors.toList());


        log.debug("Tournament population created.");
        return new Schedule[]{tournamentPopulation.get(0), tournamentPopulation.get(1)};
    }

    private Schedule crossingOver(Schedule parent1, Schedule parent2) {
        Table<ScheduleCell, Group, Class> childSchedule = HashBasedTable.create();
        crossGym(childSchedule, parent1, parent2);
        crossLectures(childSchedule, parent1, parent2);
        crossSeminars(childSchedule, parent1, parent2);
        return new Schedule(childSchedule);
    }

    private void crossGym(Table<ScheduleCell, Group, Class> childSchedule, Schedule parent1, Schedule parent2) {
        log.debug("Crossing gym");
        for (int j = 1; j < MAX_SEMESTER_NUM; j += 2) {
            List<Group> groups = scheduleParameters.getGroups();
            List<Class> gymClasses = getRandomParentSchedule(parent1, parent2).getSchedule().column(groups.get(0)).values()
                    .stream().filter(c -> c.getClassType().equals(ClassType.GYM)).distinct().collect(Collectors.toList());
            int locus;
            if (gymClasses.size() < 2) {
                locus = 1;
            } else {
                Random random = new Random();
                locus = random.nextInt(gymClasses.size() - 1);
            }
            for (int i = 0; i < gymClasses.size(); i++) {
                Class c = gymClasses.get(i);
                List<ScheduleCell> cells;
                if (i < locus)
                    cells = getCellsByGroupAndClass(parent1.getSchedule(), groups.get(0), c);
                else
                    cells = getCellsByGroupAndClass(parent2.getSchedule(), groups.get(0), c);
                for (ScheduleCell cell : cells) {
                    putLecture(childSchedule, cell, groups, c);
                }
            }
        }
    }

    private void crossSeminars(Table<ScheduleCell, Group, Class> childSchedule, Schedule parent1, Schedule parent2) {
        log.debug("Crossing seminars");
        for (int j = 1; j < MAX_SEMESTER_NUM; j += 2) {
            List<Group> groups = scheduleParameters.getGroups();
            Random random = new Random();
            int locus = random.nextInt((int) parent1.getSchedule().column(groups.get(0)).values()
                    .stream().filter(c -> c.getClassType().equals(ClassType.SEMINAR)).count() - 1);

            for (Group group : groups) {
                List<Class> seminars1 = parent1.getSchedule().column(group).values()
                        .stream().filter(c -> c.getClassType().equals(ClassType.SEMINAR)).sorted().collect(Collectors.toList());
                List<Class> seminars2 = parent2.getSchedule().column(group).values()
                        .stream().filter(c -> c.getClassType().equals(ClassType.SEMINAR)).sorted().collect(Collectors.toList());
                for (int i = 0; i < seminars1.size(); i++) {
                    Class seminar;
                    List<ScheduleCell> cells;
                    if (i < locus) {
                        seminar = seminars1.get(i);
                        cells = getCellsByGroupAndClass(parent1.getSchedule(), group, seminar);
                    } else {
                        seminar = seminars2.get(i);
                        cells = getCellsByGroupAndClass(parent2.getSchedule(), group, seminar);
                    }

                    Teacher teacher = seminars1.stream()
                            .filter(c -> c.getSubject().equals(seminar.getSubject()))
                            .map(Class::getTeacher)
                            .findAny()
                            .orElseThrow(IllegalArgumentException::new);
                    seminar.setTeacher(teacher);

                    for (ScheduleCell cell : cells) {
                        if (!childSchedule.contains(cell, group)
                                && teacherIsAvailable(childSchedule, cell, teacher)) {
                            if (!auditoriumIsAvailable(childSchedule, cell, seminar.getAuditorium())) {
                                seminar.setAuditorium(getRandomAuditorium(childSchedule, cell, group));
                            }
                            childSchedule.put(cell, group, seminar);
                        } else {
                            childSchedule.put(getEmptyRandomCellForGroup(childSchedule, group, teacher), group, seminar);
                        }

                    }

                }
            }
        }
    }

    private Auditorium getRandomAuditorium(Table<ScheduleCell, Group, Class> childSchedule, ScheduleCell cell, Group group) {
        List<Auditorium> auditoriums = auditoriumRepository.findAllByAllowedClassTypesIn(List.of(ClassType.SEMINAR));
        Random random = new Random();
        Auditorium auditorium;
        if (scheduleParameters.getGroupAuditoriumMap() != null
                && !scheduleParameters.getGroupAuditoriumMap().isEmpty()
                && auditoriumIsAvailable(childSchedule, cell, scheduleParameters.getGroupAuditoriumMap().get(group))) {
            auditorium = scheduleParameters.getGroupAuditoriumMap().get(group);
        } else {
            do {
                auditorium = auditoriums.get(random.nextInt(auditoriums.size()));
            } while (!auditoriumIsAvailable(childSchedule, cell, auditorium));
        }
        return auditorium;
    }

    private boolean auditoriumIsAvailable(Table<ScheduleCell, Group, Class> childSchedule, ScheduleCell cell, Auditorium auditorium) {
        for (Class c : childSchedule.row(cell).values()) {
            if (c.getAuditorium().equals(auditorium)) {
                return false;
            }
        }
        return true;
    }

    private boolean teacherIsAvailable(Table<ScheduleCell, Group, Class> childSchedule, ScheduleCell cell, Teacher teacher) {
        for (Class c : childSchedule.row(cell).values()) {
            if (c.getTeacher().equals(teacher)) {
                return false;
            }
        }
        return true;
    }


    private void crossLectures(Table<ScheduleCell, Group, Class> childSchedule, Schedule parent1, Schedule parent2) {
        log.debug("Crossing lectures");
        for (int j = 1; j < MAX_SEMESTER_NUM; j += 2) {
            List<Group> groups = scheduleParameters.getGroups();
            List<Class> lectures = getRandomParentSchedule(parent1, parent2).getSchedule().column(groups.get(0)).values().stream().filter(c -> c.getClassType().equals(ClassType.LECTURE)).distinct().collect(Collectors.toList());
            Random random = new Random();
            int locus = random.nextInt(lectures.size() - 1);
            for (int i = 0; i < lectures.size(); i++) {
                Class c = lectures.get(i);
                List<ScheduleCell> cells;
                if (i < locus)
                    cells = getCellsByGroupAndClass(parent1.getSchedule(), groups.get(0), c);
                else
                    cells = getCellsByGroupAndClass(parent2.getSchedule(), groups.get(0), c);
                for (ScheduleCell cell : cells) {
                    putLecture(childSchedule, cell, groups, c);
                }
            }
        }
    }

    private void putLecture(Table<ScheduleCell, Group, Class> childSchedule, ScheduleCell cell, List<Group> groups, Class c) {
        if (!rowIsEmpty(childSchedule, cell, groups)) {
            cell = getEmptyRandomRow(childSchedule, groups);
        }
        for (Group group : groups) {
            childSchedule.put(cell, group, c);
        }
    }

    private void removeLectures(Table<ScheduleCell, Group, Class> schedule, List<Group> groups, Class c) {
        List<ScheduleCell> cells = getCellsByGroupAndClass(schedule, groups.get(0), c);
        for (ScheduleCell cell : cells) {
            for (Group group : groups) {
                schedule.remove(cell, group);
            }
        }
    }

    private List<ScheduleCell> getCellsByGroupAndClass(Table<ScheduleCell, Group, Class> schedule, Group
            group, Class c) {
        List<ScheduleCell> cells = new ArrayList<>();
        Set<Map.Entry<ScheduleCell, Class>> scheduleForGroup = schedule.column(group).entrySet();
        for (Map.Entry<ScheduleCell, Class> entry : scheduleForGroup) {
            if (entry.getValue().getSubject().getName().equals(c.getSubject().getName())
                    && entry.getValue().getClassType().equals(c.getClassType())) {
                cells.add(entry.getKey());
            }
        }
        return cells;
    }

    private Schedule mutation(Schedule schedule) {
        Random random = new Random();
        for (int j = 1; j < MAX_SEMESTER_NUM; j += 2) {
            List<Group> groups = scheduleParameters.getGroups();
            for (Group group : groups) {
                Map<ScheduleCell, Class> scheduleForGroup = schedule.getSchedule().column(group);
                log.debug("Mutation for group " + group.getNumber() + " started. Count of classes: " + scheduleForGroup.values().size());
                Set<ScheduleCell> cellSet = new HashSet<>(scheduleForGroup.keySet());
                for (ScheduleCell cell : cellSet) {
                    if (random.nextInt(100) < PROBABILITY_OF_MUTATION) {
                        Class c = scheduleForGroup.get(cell);
                        //if some value was deleted in previous steps
                        if (c == null) {
                            continue;
                        }
                        log.debug("Mutation for " + cell.toString() + " and " + c.toString());
                        if (c.getClassType().equals(ClassType.SEMINAR)) {
                            List<ScheduleCell> cells = getCellsByGroupAndClass(schedule.getSchedule(), group, c);
                            for (ScheduleCell cell1 : cells) {
                                scheduleForGroup.remove(cell1);
                            }
                            for (int i = 0; i < cells.size(); i++) {
                                scheduleForGroup.put(getEmptyRandomCellForGroup(schedule.getSchedule(), group, c.getTeacher()), c);
                            }
                        } else {
                            int countOfClasses = getCellsByGroupAndClass(schedule.getSchedule(), group, c).size();
                            removeLectures(schedule.getSchedule(), groups, c);
                            for (int i = 0; i < countOfClasses; i++) {
                                ScheduleCell emptySellForCourse = getEmptyRandomRow(schedule.getSchedule(), groups);
                                putLecture(schedule.getSchedule(), emptySellForCourse, groups, c);
                            }
                        }
                    }
                }
            }
        }
        return schedule;
    }

    private ScheduleCell getEmptyRandomCellForGroup(Table<ScheduleCell, Group, Class> schedule, Group group, Teacher teacher) {
        Random random = new Random();
        ScheduleCell emptyCell;
        do {
            DayOfWeek dayOfWeek = getRandomDayOfWeek(ClassType.SEMINAR);
            List<TimeSlot> timeSlots = scheduleParameters.getTimeSlots();
            TimeSlot timeSlot = timeSlots.get(random.nextInt(timeSlots.size()));
            emptyCell = ScheduleCell.builder()
                    .dayOfWeek(dayOfWeek)
                    .timeSlot(timeSlot)
                    .build();
        } while (schedule.column(group).containsKey(emptyCell) || !teacherIsAvailable(schedule, emptyCell, teacher));
        return emptyCell;
    }

    private ScheduleCell getEmptyRandomRow(Table<ScheduleCell, Group, Class> childSchedule, List<Group> groups) {
        Random random = new Random();
        List<TimeSlot> timeSlots = scheduleParameters.getTimeSlots();
        ScheduleCell emptyCell;
        do {
            DayOfWeek dayOfWeek = getRandomDayOfWeek(ClassType.LECTURE);
            TimeSlot timeSlot = timeSlots.get(random.nextInt(timeSlots.size()));
            emptyCell = ScheduleCell.builder()
                    .dayOfWeek(dayOfWeek)
                    .timeSlot(timeSlot)
                    .build();
        } while (!rowIsEmpty(childSchedule, emptyCell, groups));
        return emptyCell;
    }


    private DayOfWeek getRandomDayOfWeek(ClassType classType) {
        Random random = new Random();
        if (scheduleParameters.getLectureDays() == null || scheduleParameters.getLectureDays().isEmpty()) {
            return DayOfWeek.values()[random.nextInt(DayOfWeek.values().length)];
        } else if (classType.equals(ClassType.LECTURE) || classType.equals(ClassType.GYM)) {
            return scheduleParameters.getLectureDays().get(random.nextInt(scheduleParameters.getLectureDays().size()));
        } else {
            List<DayOfWeek> seminarDays = Arrays.stream(DayOfWeek.values().clone())
                    .filter(day -> !scheduleParameters.getLectureDays().contains(day))
                    .collect(Collectors.toList());
            return seminarDays.get(random.nextInt(seminarDays.size()));
        }


    }

    private boolean rowIsEmpty(Table<ScheduleCell, Group, Class> childSchedule, ScheduleCell cell, List<Group> groups) {
        for (Group group : groups) {
            if (childSchedule.contains(cell, group))
                return false;
        }
        return true;
    }

    private Schedule getRandomParentSchedule(Schedule parent1, Schedule parent2) {
        Random random = new Random();
        if (random.nextInt(2) == 0) {
            return parent1;
        } else {
            return parent2;
        }
    }
}
