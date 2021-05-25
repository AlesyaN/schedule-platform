package ru.itis.scheduleplatform.models;

import lombok.Data;
import ru.itis.scheduleplatform.constants.Const;
import ru.itis.scheduleplatform.enums.ClassType;

import javax.persistence.*;
import java.util.Map;

@Entity
@Data
@Table(name = "study_plan")
public class StudyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "class_type")
    @MapKeyClass(ClassType.class)
    @MapKeyEnumerated(EnumType.STRING)
    Map<ClassType, Integer> hours;

    public Integer getCountOfClassesPerWeek(ClassType type) {
        if (hours.get(type) != null) {
            int count = hours.get(type) / (2 * Const.COUNT_OF_WEEKS_IN_SEMESTER);
            return Math.max(count, 1);
        }
        return 0;
    }

    public Integer getCountOfClassesPerWeek() {
        int count = 0;
        for (ClassType classType: ClassType.values()) {
            count += getCountOfClassesPerWeek(classType);
        }
        return count;
    }
}
