package hhplus.lecture.presentation.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ApplyLectureItemResponse {

    private String lectureName;
    private String tutorName;
    private String userName;
    private LocalDateTime applyTime;

    @Builder
    public ApplyLectureItemResponse(String lectureName, String tutorName, String userName, LocalDateTime applyTime) {
        this.lectureName = lectureName;
        this.tutorName = tutorName;
        this.userName = userName;
        this.applyTime = applyTime;
    }
}
