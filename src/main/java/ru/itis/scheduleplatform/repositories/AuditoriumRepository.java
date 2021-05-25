package ru.itis.scheduleplatform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.scheduleplatform.enums.ClassType;
import ru.itis.scheduleplatform.models.Auditorium;

import java.util.List;

public interface AuditoriumRepository extends JpaRepository<Auditorium, Long> {

    List<Auditorium> findAllByAllowedClassTypesIn(List<ClassType> allowedClassTypes);

}
