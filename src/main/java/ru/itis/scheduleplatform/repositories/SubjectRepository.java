package ru.itis.scheduleplatform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.scheduleplatform.models.Subject;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllByName(String name);
}
