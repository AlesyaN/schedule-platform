package ru.itis.scheduleplatform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.scheduleplatform.models.Semester;

import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Long> {
    Optional<Semester> findByNumber(Integer number);
}
