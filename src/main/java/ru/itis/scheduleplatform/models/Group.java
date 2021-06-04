package ru.itis.scheduleplatform.models;

import lombok.Data;
import org.springframework.data.annotation.Transient;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@Table(name = "\"group\"")
public class Group implements Comparable<Group> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    @Transient
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "grade_id")
    @Transient
    private Grade grade;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(number, group.number) &&
                Objects.equals(count, group.count) &&
                Objects.equals(grade, group.grade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, count, grade);
    }

    @Override
    public int compareTo(Group group) {
        return this.number.compareTo(group.getNumber());
    }
}
