package hhplus.lecture.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRegistrationRequest {

    @NotEmpty(message = "유저 값은 필수 입니다.")
    private Long userId;
}
