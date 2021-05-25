package ru.itis.scheduleplatform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.scheduleplatform.models.TimeSlot;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
}
