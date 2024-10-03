package hhplus.lecture.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchLectureItemRequest {

    private Long lectureId;

    @Min(value = 2024, message = "2024년 이후 강의만 조회 가능 합니다.")
    @Max(value = 2026, message = "2026년 이전 강의만 조회 가능 합니다.")
    private Integer year;

    @Min(value = 1, message = "1 ~ 12월 사이의 값만 허용 합니다. (빈 값 일시 전체 검색)")
    @Max(value = 12, message = "1 ~ 12월 사이의 값만 허용 합니다. (빈 값 일시 전체 검색)")
    private Integer month;

    @Min(value = 1, message = "1 ~ 31 일 사이의 값만 허용 합니다. (빈 값 일시 전체 검색)")
    @Max(value = 31,message = "1 ~ 31 일 사이의 값만 허용 합니다. (빈 값 일시 전체 검색)")
    private Integer day;
}
