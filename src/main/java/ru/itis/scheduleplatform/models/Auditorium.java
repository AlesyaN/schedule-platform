package ru.itis.scheduleplatform.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.itis.scheduleplatform.enums.ClassType;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@Table(name = "auditorium")
public class Auditorium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Building building;

    private String roomNumber;
    private Integer capacity;

    @JsonIgnore
    @ElementCollection(targetClass = ClassType.class)
    @JoinTable(name = "auditorium_types", joinColumns = @JoinColumn(name = "auditorium_id"))
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<ClassType> allowedClassTypes;


    @Override
    public String toString() {
        return roomNumber;
    }
}
