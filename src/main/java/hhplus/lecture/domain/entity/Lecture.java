package hhplus.lecture.domain.entity;

import hhplus.common.config.jpa.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long lectureId;

    @Column
    private String name;

    public Lecture(String name){
        this.name = name;
    }
}
