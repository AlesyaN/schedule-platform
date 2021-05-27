package ru.itis.scheduleplatform.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.itis.scheduleplatform.enums.ClassType;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@Entity
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private String patronymic;


    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "teacher", cascade = CascadeType.ALL)
    @MapKeyJoinColumn(name = "subject_id")
    private Map<Subject, ClassTypesForTeacherAndSubject> subjects;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(id, teacher.id) && Objects.equals(name, teacher.name) && Objects.equals(surname, teacher.surname) && Objects.equals(patronymic, teacher.patronymic);
    }

    @Override
    public String toString() {
        return surname;
    }
}
