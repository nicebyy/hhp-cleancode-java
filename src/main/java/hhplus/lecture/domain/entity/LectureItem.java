package hhplus.lecture.domain.entity;

import hhplus.common.config.jpa.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureItem{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long lectureItemId;

    @Column
    private Integer totalCapacity;

    @Column
    private Integer currentCapacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    private LocalDateTime startTime;

    public LectureItem(Lecture lecture, Integer capacity, LocalDateTime startTime){
        this.totalCapacity = capacity;
        this.currentCapacity = 0;
        this.lecture = lecture;
        this.startTime = startTime;
    }
}
