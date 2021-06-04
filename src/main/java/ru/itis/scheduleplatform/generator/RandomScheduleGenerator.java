package ru.itis.scheduleplatform.generator;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.ScheduleParameters;
import ru.itis.scheduleplatform.enums.ClassType;
import ru.itis.scheduleplatform.enums.DayOfWeek;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.*;
import ru.itis.scheduleplatform.repositories.AuditoriumRepository;
import ru.itis.scheduleplatform.repositories.StudyPlanRepository;
import ru.itis.scheduleplatform.repositories.TeacherRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@Slf4j
@Service
public class RandomScheduleGenerator {

    private Table<ScheduleCell, Group, Class> schedule;

    private StudyPlanRepository studyPlanRepository;
    private TeacherRepository teacherRepository;
    private AuditoriumRepository auditoriumRepository;

    private ScheduleParameters scheduleParameters;

    public RandomScheduleGenerator(StudyPlanRepository studyPlanRepository,
                                   TeacherRepository teacherRepository,
                                   AuditoriumRepository auditoriumRepository) {
        this.studyPlanRepository = studyPlanRepository;
        this.teacherRepository = teacherRepository;
        this.auditoriumRepository = auditoriumRepository;
    }

    public Table<ScheduleCell, Group, Class> generate(ScheduleParameters scheduleParameters) {
        this.scheduleParameters = scheduleParameters;
        log.debug("Generating random schedule sample");
        schedule = HashBasedTable.create(scheduleParameters.getScheduleTable());
        assignGym(1);
        assignLectures(1);
        assignSeminars(1);
        return schedule;
    }

    private void assignGym(Integer semesterNumber) {
        log.debug("Assign GYM");
        List<StudyPlan> gymClasses = studyPlanRepository.findAllBySemesterNumberAndClassType(semesterNumber, ClassType.GYM.toString());
        for (StudyPlan gym : gymClasses) {
            List<Group> groups = scheduleParameters.getGroups();
            for (int i = 0; i < gym.getCountOfClassesPerWeek(ClassType.GYM); i++) {
                Teacher teacher = getRandomTeacherForSubjectAndClassType(gym.getSubject(), ClassType.GYM);
                ScheduleCell randomCell = getEmptyRandomCell(groups, teacher);
                Auditorium auditorium = getRandomLectureAuditorium(ClassType.GYM, randomCell);
                for (Group group : groups) {
                    Class c = Class.builder()
                            .classType(ClassType.GYM)
                            .subject(gym.getSubject())
                            .teacher(teacher)
                            .auditorium(auditorium)
                            .build();
                    schedule.put(randomCell, group, c);
                }
            }
        }
    }

    private void assignSeminars(Integer semesterNumber) {
        log.debug("Assigning seminars");
        List<StudyPlan> seminars = studyPlanRepository.findAllBySemesterNumberAndClassType(semesterNumber, ClassType.SEMINAR.toString());
        for (StudyPlan seminar : seminars) {
            List<Group> groups = scheduleParameters.getGroups();
            for (Group group : groups) {
                for (int i = 0; i < seminar.getCountOfClassesPerWeek(ClassType.SEMINAR); i++) {
                    Teacher teacher = getRandomTeacherForSubjectAndClassType(seminar.getSubject(), ClassType.SEMINAR);
                    ScheduleCell randomCell = getEmptyRandomCell(group, teacher);
                    Auditorium auditorium = getRandomSeminarAuditorium(randomCell, group);
                    Class c = Class.builder()
                            .classType(ClassType.SEMINAR)
                            .subject(seminar.getSubject())
                            .teacher(teacher)
                            .auditorium(auditorium)
                            .build();
                    schedule.put(randomCell, group, c);
                }
            }
        }
    }


    private void assignLectures(Integer semesterNumber) {
        log.debug("Assigning lectures");
        List<StudyPlan> lectures = studyPlanRepository.findAllBySemesterNumberAndClassType(semesterNumber, ClassType.LECTURE.toString());
        for (StudyPlan lecture : lectures) {
            List<Group> groups = scheduleParameters.getGroups();
            for (int i = 0; i < lecture.getCountOfClassesPerWeek(ClassType.LECTURE); i++) {
                Teacher teacher = getRandomTeacherForSubjectAndClassType(lecture.getSubject(), ClassType.LECTURE);
                ScheduleCell randomCell = getEmptyRandomCell(groups, teacher);
                Auditorium auditorium = getRandomLectureAuditorium(ClassType.LECTURE, randomCell);
                for (Group group : groups) {
                    Class c = Class.builder()
                            .classType(ClassType.LECTURE)
                            .subject(lecture.getSubject())
                            .teacher(teacher)
                            .auditorium(auditorium)
                            .build();
                    schedule.put(randomCell, group, c);
                }
            }
        }
    }

    private ScheduleCell getEmptyRandomCell(Group group, Teacher teacher) {
        Random random = new Random();
        ScheduleCell scheduleCell;
        do {
            DayOfWeek dayOfWeek = getRandomDayOfWeek(ClassType.SEMINAR);
            List<TimeSlot> timeSlots = scheduleParameters.getTimeSlots();
            TimeSlot timeSlot = timeSlots.get(random.nextInt(timeSlots.size()));
            scheduleCell = ScheduleCell.builder()
                    .dayOfWeek(dayOfWeek)
                    .timeSlot(timeSlot)
                    .build();
        } while (schedule.contains(scheduleCell, group) || !teacherIsFree(teacher, scheduleCell));
        return scheduleCell;
    }

    private boolean teacherIsFree(Teacher teacher, ScheduleCell scheduleCell) {
        for (Class c : schedule.row(scheduleCell).values()) {
            if (c.getTeacher().equals(teacher)) {
                return false;
            }
        }
        return true;
    }

    private ScheduleCell getEmptyRandomCell(List<Group> groups, Teacher teacher) {
        Random random = new Random();
        ScheduleCell scheduleCell;
        do {
            DayOfWeek dayOfWeek = getRandomDayOfWeek(ClassType.LECTURE);
            List<TimeSlot> timeSlots = scheduleParameters.getTimeSlots();
            TimeSlot timeSlot = timeSlots.get(random.nextInt(timeSlots.size()));
            scheduleCell = ScheduleCell.builder()
                    .dayOfWeek(dayOfWeek)
                    .timeSlot(timeSlot)
                    .build();
        } while (!cellIsEmpty(scheduleCell, groups) || !teacherIsFree(teacher, scheduleCell));
        return scheduleCell;
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

    private boolean cellIsEmpty(ScheduleCell cell, List<Group> groups) {
        for (Group group : groups) {
            if (schedule.contains(cell, group))
                return false;
        }
        return true;
    }

    private Teacher getRandomTeacherForSubjectAndClassType(Subject subject, ClassType classType) {
        List<Teacher> teachers = teacherRepository.findBySubjectAndClassType(subject.getId(), classType.toString());
        Random random = new Random();
        return teachers.get(random.nextInt(teachers.size()));
    }

    private Auditorium getRandomLectureAuditorium(ClassType classType, ScheduleCell scheduleCell) {
        List<Auditorium> auditoriums = auditoriumRepository.findAllByAllowedClassTypesIn(List.of(classType));
        Random random = new Random();
        Auditorium auditorium;
        do {
            auditorium = auditoriums.get(random.nextInt(auditoriums.size()));
        } while (!auditoriumIsFree(auditorium, scheduleCell));
        return auditorium;
    }

    private Auditorium getRandomSeminarAuditorium(ScheduleCell scheduleCell, Group group) {
        List<Auditorium> auditoriums = auditoriumRepository.findAllByAllowedClassTypesIn(List.of(ClassType.SEMINAR));
        Random random = new Random();
        Auditorium auditorium;
        if (scheduleParameters.getGroupAuditoriumMap() != null
                && !scheduleParameters.getGroupAuditoriumMap().isEmpty()
                && auditoriumIsFree(scheduleParameters.getGroupAuditoriumMap().get(group), scheduleCell)) {
            auditorium = scheduleParameters.getGroupAuditoriumMap().get(group);
        } else {
            do {
                auditorium = auditoriums.get(random.nextInt(auditoriums.size()));
            } while (!auditoriumIsFree(auditorium, scheduleCell));
        }
        return auditorium;
    }

    private boolean auditoriumIsFree(Auditorium auditorium, ScheduleCell scheduleCell) {
        for (Class c : schedule.row(scheduleCell).values()) {
            if (c.getAuditorium().equals(auditorium)) {
                return false;
            }
        }
        return true;
    }

}
