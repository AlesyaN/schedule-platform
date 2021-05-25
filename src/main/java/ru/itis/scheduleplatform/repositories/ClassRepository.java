package ru.itis.scheduleplatform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.scheduleplatform.models.Class;

public interface ClassRepository extends JpaRepository<Class, Long> {
}
