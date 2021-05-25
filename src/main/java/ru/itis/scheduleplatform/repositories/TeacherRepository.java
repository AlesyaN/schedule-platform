package ru.itis.scheduleplatform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.scheduleplatform.models.Teacher;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Query(nativeQuery = true, value = "SELECT t.id, t.name, t.patronymic, t.surname  " +
            "FROM subject_teacher st INNER JOIN teacher t ON st.teacher_id = t.id WHERE st.subject_id = :subjectId")
    List<Teacher> findBySubject(@Param("subjectId") Long subjectId);

    @Query(nativeQuery = true, value = "SELECT j.id, j.name, j.patronymic, j.surname  " +
            "FROM (SELECT t.id, t.name, t.patronymic, t.surname, st.id as st_id FROM subject_teacher st " +
            "INNER JOIN teacher t ON st.teacher_id = t.id WHERE st.subject_id = :subjectId) j " +
            "INNER JOIN subject_teacher_classtype stc ON j.st_id = stc.subject_teacher_id WHERE stc.class_type = :classType")
    List<Teacher> findBySubjectAndClassType(@Param("subjectId") Long subjectId, @Param("classType") String classType);


}
