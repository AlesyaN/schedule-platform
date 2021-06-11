package ru.itis.scheduleplatform.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import ru.itis.scheduleplatform.enums.ClassType;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auditorium")
public class Auditorium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Transient
    private Building building;

    private String roomNumber;

    @Transient
    private Integer capacity;

    @JsonIgnore
    @ElementCollection(targetClass = ClassType.class)
    @JoinTable(name = "auditorium_types", joinColumns = @JoinColumn(name = "auditorium_id"))
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Transient
    private List<ClassType> allowedClassTypes;


    @Override
    public String toString() {
        return roomNumber;
    }
}
