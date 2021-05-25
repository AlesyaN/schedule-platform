package ru.itis.scheduleplatform.models;

import lombok.Builder;
import lombok.Data;
import ru.itis.scheduleplatform.enums.ClassType;

import javax.persistence.*;
import java.util.Objects;

@Data
@Builder
@Entity
public class Class implements Comparable<Class> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "auditorium_id")
    private Auditorium auditorium;

    @Enumerated(EnumType.STRING)
    private ClassType classType;

    @Override
    public String toString() {
        return "Class{" +
                "subject=" + subject.getName() +
                ", classType=" + classType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Class aClass = (Class) o;
        return Objects.equals(subject, aClass.subject) &&
                Objects.equals(teacher, aClass.teacher) &&
                Objects.equals(auditorium, aClass.auditorium) &&
                classType == aClass.classType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, teacher, auditorium, classType);
    }

    @Override
    public int compareTo(Class o) {
        long diff = subject.getId() - o.getSubject().getId();
        if (diff == 0) {
            diff = classType.compareTo(o.getClassType());
        }
        return (int) diff;
    }
}
