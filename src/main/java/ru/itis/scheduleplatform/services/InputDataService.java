package ru.itis.scheduleplatform.services;

import org.springframework.stereotype.Service;
import ru.itis.scheduleplatform.dto.GeneratorParameters;
import ru.itis.scheduleplatform.enums.ClassType;
import ru.itis.scheduleplatform.models.Auditorium;
import ru.itis.scheduleplatform.models.ClassTypesForTeacherAndSubject;
import ru.itis.scheduleplatform.models.Subject;
import ru.itis.scheduleplatform.models.Teacher;
import ru.itis.scheduleplatform.repositories.AuditoriumRepository;
import ru.itis.scheduleplatform.repositories.GroupRepository;
import ru.itis.scheduleplatform.repositories.SubjectRepository;
import ru.itis.scheduleplatform.repositories.TeacherRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InputDataService {

    private AuditoriumRepository auditoriumRepository;
    private GroupRepository groupRepository;
    private TeacherRepository teacherRepository;
    private SubjectRepository subjectRepository;

    public InputDataService(AuditoriumRepository auditoriumRepository, GroupRepository groupRepository, TeacherRepository teacherRepository, SubjectRepository subjectRepository) {
        this.auditoriumRepository = auditoriumRepository;
        this.groupRepository = groupRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
    }

    public void saveInputData(GeneratorParameters.InputData inputData) {
        auditoriumRepository.deleteAll();
        groupRepository.deleteAll();
        teacherRepository.deleteAll();

        List<Auditorium> auditoriums = inputData.getAuditoriums().stream().map(dto -> {
            return Auditorium.builder()
                    .roomNumber(dto.getRoomNumber())
                    .allowedClassTypes(dto.getAllowedClassTypes().stream().map(ClassType::valueOf).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());

        List<Teacher> teachers = inputData.getTeachers().stream().map(dto -> {
            Map<Subject, ClassTypesForTeacherAndSubject> subjects = new HashMap<>();
            Teacher teacher = new Teacher();
            teacher.setName(dto.getName());
            teacher.setSurname(dto.getSurname());
            teacher.setPatronymic(dto.getPatronymic());
            for (GeneratorParameters.InputData.TeacherDto.SubjectClassType subjectClassType : dto.getSubjects()) {
                Subject subject = subjectRepository.findAllByName(subjectClassType.getSubject()).get(0);
                ClassTypesForTeacherAndSubject classTypesForTeacherAndSubject = new ClassTypesForTeacherAndSubject();
                classTypesForTeacherAndSubject.setSubject(subject);
                classTypesForTeacherAndSubject.setTeacher(teacher);
                classTypesForTeacherAndSubject.setClassTypes(subjectClassType.getClassTypes().stream().map(ClassType::valueOf).collect(Collectors.toList()));
                subjects.put(subject, classTypesForTeacherAndSubject);
            }
            teacher.setSubjects(subjects);
            return teacher;
        }).collect(Collectors.toList());

        auditoriumRepository.saveAll(auditoriums);
        groupRepository.saveAll(inputData.getGroups());
        teacherRepository.saveAll(teachers);
    }
}
