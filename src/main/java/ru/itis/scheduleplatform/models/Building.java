package ru.itis.scheduleplatform.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Type(type = "text")
    private String address;

    @JsonIgnore
    @OneToMany(mappedBy = "building")
    List<Auditorium> auditoriums;

}
