package hhplus.lecture.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyLectureItemRequest {

    @NotNull(message = "유저 값은 필수 입니다.")
    private Long userId;

    @NotNull(message = "강의 값은 필수 입니다.")
    private Long lectureItemId;
}
