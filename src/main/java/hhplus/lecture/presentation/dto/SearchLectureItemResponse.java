package hhplus.lecture.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SearchLectureItemResponse {

    private Long lectureId;
    private String lectureName;
    private String tutorName;
    private LocalDateTime startTime;
    private Integer totalCapacity;
    private Integer currentCapacity;

    @Builder
    public SearchLectureItemResponse(Long lectureId, String lectureName, String tutorName, LocalDateTime startTime, Integer totalCapacity, Integer currentCapacity) {
        this.lectureId = lectureId;
        this.lectureName = lectureName;
        this.tutorName = tutorName;
        this.startTime = startTime;
        this.totalCapacity = totalCapacity;
        this.currentCapacity = currentCapacity;
    }
}
