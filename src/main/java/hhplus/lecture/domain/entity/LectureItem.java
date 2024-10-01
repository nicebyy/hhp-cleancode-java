package hhplus.lecture.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
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

    @Column
    private LocalDateTime startTime;

    public LectureItem(Lecture lecture, Integer capacity, LocalDateTime startTime){
        this.totalCapacity = capacity;
        this.currentCapacity = 0;
        this.lecture = lecture;
        this.startTime = startTime;
    }

    public void subtractCurrentCapacity(){
        this.currentCapacity--;
    }
}
