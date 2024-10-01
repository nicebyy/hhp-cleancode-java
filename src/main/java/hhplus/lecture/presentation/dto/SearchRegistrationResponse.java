package hhplus.lecture.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SearchRegistrationResponse {

    private String userName;
    private List<AppliedRegistrationResponse> appliedRegistrationResponseList = new ArrayList<>();

    @Builder
    public SearchRegistrationResponse(String userName, List<AppliedRegistrationResponse> appliedRegistrationResponseList) {
        this.userName = userName;
        this.appliedRegistrationResponseList = appliedRegistrationResponseList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppliedRegistrationResponse{
        private String lectureName;
        private String tutorName;
        private LocalDateTime startTime;
        private LocalDateTime applyTime;
    }
}
