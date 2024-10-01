package hhplus.lecture.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchLectureItemRequest {

    private Long lectureId;
    private Integer year;
    private Integer month;
    private Integer day;
}
