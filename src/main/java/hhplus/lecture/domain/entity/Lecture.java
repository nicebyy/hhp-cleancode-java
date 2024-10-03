package hhplus.lecture.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long lectureId;

    @Column
    private String lectureName;

    @Column
    private String tutorName;

    public Lecture(String lectureName, String tutorName){
        this.lectureName = lectureName;
        this.tutorName = tutorName;
    }
}
