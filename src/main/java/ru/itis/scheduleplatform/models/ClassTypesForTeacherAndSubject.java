package ru.itis.scheduleplatform.models;

import lombok.Data;
import ru.itis.scheduleplatform.enums.ClassType;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "subject_teacher")
public class ClassTypesForTeacherAndSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ElementCollection(targetClass = ClassType.class, fetch = FetchType.EAGER)
    @JoinTable(name = "subject_teacher_classtype", joinColumns = @JoinColumn(name = "subject_teacher_id"))
    @Column(name = "class_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<ClassType> classTypes;

    @Override
    public String toString() {
        return "ClassTypesForTeacherAndSubject{" +
                "id=" + id +
                ", teacher=" + teacher +
//                ", subject=" + subject +
                ", classTypes=" + classTypes +
                '}';
    }
}
