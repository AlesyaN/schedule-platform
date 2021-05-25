package ru.itis.scheduleplatform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.scheduleplatform.models.Group;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query(nativeQuery = true, value = "SELECT g.* FROM semester s " +
            "JOIN \"group\" g  on s.grade_id = g.grade_id " +
            "WHERE s.number = :semesterNumber ")
    List<Group> findAllBySemesterNumber(@Param("semesterNumber") Integer semesterNumber);
}
