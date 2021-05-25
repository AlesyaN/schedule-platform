package ru.itis.scheduleplatform.models;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Subject() {

    }

    public Subject(String name) {
        this.name = name;
    }
}
