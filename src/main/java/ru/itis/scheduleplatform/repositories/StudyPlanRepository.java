package ru.itis.scheduleplatform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itis.scheduleplatform.models.StudyPlan;

import java.util.List;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {

    @Query(nativeQuery = true, value = "SELECT study_plan.id, study_plan.semester_id, study_plan.subject_id" +
            " FROM study_plan INNER JOIN " +
            " (SELECT sph.study_plan_id FROM study_plan_hours sph WHERE class_type = 'LECTURE') AS ids" +
            " ON study_plan.id = ids.study_plan_id;")
    List<StudyPlan> findAllLecturePlans();

    @Query(nativeQuery = true, value = "SELECT sp.id, sp.semester_id, sp.subject_id" +
            " FROM (SELECT study_plan.id, study_plan.semester_id, study_plan.subject_id " +
            " FROM study_plan WHERE study_plan.semester_id = :semesterId) as sp INNER JOIN " +
            " (select sph.study_plan_id FROM study_plan_hours sph WHERE class_type = :classType) AS ids" +
            "  ON sp.id = ids.study_plan_id")
    List<StudyPlan> findAllBySemesterIdAndClassType(@Param("semesterId") Long semesterId, @Param("classType") String classType);

    @Query(nativeQuery = true, value = " SELECT sp.id, sp.semester_id, sp.subject_id " +
            "FROM (SELECT sp.id, sp.semester_id, sp.subject_id " +
            "FROM study_plan sp INNER JOIN semester AS s on sp.semester_id = s.id " +
            "WHERE (s.number % 2) = :number % 2) as sp INNER JOIN " +
            "(SELECT sph.study_plan_id FROM study_plan_hours sph WHERE class_type = :classType) AS ids " +
            "ON sp.id = ids.study_plan_id")
    List<StudyPlan> findAllBySemesterNumberAndClassType(@Param("number") Integer semesterNumber, @Param("classType") String classType);

    @Query(nativeQuery = true, value = "SELECT sp.id, sp.semester_id, sp.subject_id " +
            "      FROM study_plan sp " +
            "               INNER JOIN semester AS s on sp.semester_id = s.id " +
            "      WHERE s.number = :number")
    List<StudyPlan> findAllBySemesterNumber(@Param("number") Integer semesterNumber);

    @Query(nativeQuery = true, value = " SELECT sp.id, sp.semester_id, sp.subject_id " +
            "FROM (SELECT sp.id, sp.semester_id, sp.subject_id " +
            "FROM study_plan sp INNER JOIN semester AS s on sp.semester_id = s.id " +
            "WHERE s.number = :number ) as sp INNER JOIN " +
            "(SELECT sph.study_plan_id FROM study_plan_hours sph WHERE class_type = :classType) AS ids " +
            "ON sp.id = ids.study_plan_id")
    List<StudyPlan> findAllByActualSemesterNumberAndClassType(@Param("number") Integer semesterNumber, @Param("classType") String classType);
}
