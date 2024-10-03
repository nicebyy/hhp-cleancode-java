package hhplus.lecture.presentation;

import hhplus.common.api.ApiControllerAdvice;
import hhplus.common.api.ApiResponse;
import hhplus.common.exception.BusinessException;
import hhplus.lecture.application.LectureFacade;
import hhplus.lecture.presentation.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static hhplus.common.enums.ResponseCodeEnum.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LectureController.class)
@Import({ApiResponse.class, ApiControllerAdvice.class}) // 글로벌 예외 처리기 추가
class LectureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LectureFacade lectureFacade;

    /**
     * 유효한 요청으로 강의 아이템 목록을 조회하는 테스트
     */
    @Test
    @DisplayName("유효한 요청으로 강의 아이템 목록을 조회한다.")
    void searchLectureItemSearch_validRequest() throws Exception {
        // Given
        SearchLectureItemRequest request = new SearchLectureItemRequest();
        request.setLectureId(100L);
        request.setYear(2024);
        request.setMonth(10);
        request.setDay(5);

        // Mocked response
        Map<LocalDateTime, List<SearchLectureItemResponse>> mockResponse = new TreeMap<>();
        mockResponse.put(LocalDateTime.of(2024, 10, 5, 10, 0),
                Arrays.asList(SearchLectureItemResponse.builder()
                        .lectureId(100L)
                        .lectureName("테스트 강의")
                        .tutorName("홍길동")
                        .startTime(LocalDateTime.of(2024, 10, 5, 10, 0))
                        .totalCapacity(30)
                        .currentCapacity(10)
                        .build()));
        mockResponse.put(LocalDateTime.of(2024, 10, 6, 10, 0),
                Arrays.asList(SearchLectureItemResponse.builder()
                        .lectureId(100L)
                        .lectureName("테스트 강의")
                        .tutorName("홍길동")
                        .startTime(LocalDateTime.of(2024, 10, 6, 10, 0))
                        .totalCapacity(30)
                        .currentCapacity(20)
                        .build()));

        given(lectureFacade.findLectureItems(any(SearchLectureItemRequest.class))).willReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/lecture")
                        .param("lectureId", "100")
                        .param("year", "2024")
                        .param("month", "10")
                        .param("day", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.output", is(SUCCESS.getCode())))
                .andExpect(jsonPath("$.response.result", is(SUCCESS.getMessage())))
                .andExpect(jsonPath("$.data.*", hasSize(2))) // 맵의 크기 검증
                // 첫 번째 날짜 검증
                .andExpect(jsonPath("$.data['2024-10-05T10:00'].length()", is(1)))
                .andExpect(jsonPath("$.data['2024-10-05T10:00'][0].lectureId", is(100)))
                .andExpect(jsonPath("$.data['2024-10-05T10:00'][0].lectureName", is("테스트 강의")))
                .andExpect(jsonPath("$.data['2024-10-05T10:00'][0].tutorName", is("홍길동")))
                .andExpect(jsonPath("$.data['2024-10-05T10:00'][0].startTime", is("2024-10-05T10:00:00")))
                .andExpect(jsonPath("$.data['2024-10-05T10:00'][0].totalCapacity", is(30)))
                .andExpect(jsonPath("$.data['2024-10-05T10:00'][0].currentCapacity", is(10)))
                // 두 번째 날짜 검증
                .andExpect(jsonPath("$.data['2024-10-06T10:00'].length()", is(1)))
                .andExpect(jsonPath("$.data['2024-10-06T10:00'][0].lectureId", is(100)))
                .andExpect(jsonPath("$.data['2024-10-06T10:00'][0].lectureName", is("테스트 강의")))
                .andExpect(jsonPath("$.data['2024-10-06T10:00'][0].tutorName", is("홍길동")))
                .andExpect(jsonPath("$.data['2024-10-06T10:00'][0].startTime", is("2024-10-06T10:00:00")))
                .andExpect(jsonPath("$.data['2024-10-06T10:00'][0].totalCapacity", is(30)))
                .andExpect(jsonPath("$.data['2024-10-06T10:00'][0].currentCapacity", is(20)));
    }

    /**
     * 유효하지 않은 요청 - year가 최소값보다 작을 때
     */
    @Test
    @DisplayName("year가 최소값보다 작을 때 유효성 검증 실패")
    void searchLectureItemSearch_invalidYearTooSmall() throws Exception {
        mockMvc.perform(get("/lecture")
                        .param("lectureId", "100")
                        .param("year", "2023") // 최소값 2024
                        .param("month", "10")
                        .param("day", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response.output", is(VALIDATION_ERROR.getCode())))
                .andExpect(jsonPath("$.response.result", is(VALIDATION_ERROR.getMessage())));
    }

    /**
     * 유효하지 않은 요청 - month가 최대값보다 클 때
     */
    @Test
    @DisplayName("month가 최대값보다 클 때 유효성 검증 실패")
    void searchLectureItemSearch_invalidMonthTooLarge() throws Exception {
        mockMvc.perform(get("/lecture")
                        .param("lectureId", "100")
                        .param("year", "2024")
                        .param("month", "13") // 최대값 12
                        .param("day", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response.output", is(VALIDATION_ERROR.getCode())))
                .andExpect(jsonPath("$.response.result", is(VALIDATION_ERROR.getMessage())));
    }

    /**
     * 유효하지 않은 요청 - day가 최소값보다 작을 때
     */
    @Test
    @DisplayName("day가 최소값보다 작을 때 유효성 검증 실패")
    void searchLectureItemSearch_invalidDayTooSmall() throws Exception {
        mockMvc.perform(get("/lecture")
                        .param("lectureId", "100")
                        .param("year", "2024")
                        .param("month", "10")
                        .param("day", "0")) // 최소값 1
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response.output", is(VALIDATION_ERROR.getCode())))
                .andExpect(jsonPath("$.response.result", is(VALIDATION_ERROR.getMessage())));
    }

    /**
     * 유효하지 않은 요청 - day가 최대값보다 클 때
     */
    @Test
    @DisplayName("day가 최대값보다 클 때 유효성 검증 실패")
    void searchLectureItemSearch_invalidDayTooLarge() throws Exception {
        mockMvc.perform(get("/lecture")
                        .param("lectureId", "100")
                        .param("year", "2024")
                        .param("month", "10")
                        .param("day", "32")) // 최대값 31
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response.output", is(VALIDATION_ERROR.getCode())))
                .andExpect(jsonPath("$.response.result", is(VALIDATION_ERROR.getMessage())));
    }

    /**
     * 유효한 요청으로 강의 신청을 하는 테스트
     */
    @Test
    @DisplayName("유효한 요청으로 강의 신청을 한다.")
    void applyLecture_validRequest() throws Exception {
        // Given
        String requestBody = "{\"userId\":1,\"lectureItemId\":1000}";

        ApplyLectureItemResponse mockApplyResponse = ApplyLectureItemResponse.builder()
                .lectureName("테스트 강의")
                .tutorName("홍길동")
                .userName("이순신")
                .applyTime(LocalDateTime.of(2024, 10, 1, 12, 0))
                .build();

        given(lectureFacade.applyLectureItem(any(ApplyLectureItemRequest.class))).willReturn(mockApplyResponse);

        // When & Then
        mockMvc.perform(post("/lecture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.output", is(SUCCESS.getCode())))
                .andExpect(jsonPath("$.response.result", is(SUCCESS.getMessage())))
                .andExpect(jsonPath("$.data.lectureName", is("테스트 강의")))
                .andExpect(jsonPath("$.data.tutorName", is("홍길동")))
                .andExpect(jsonPath("$.data.userName", is("이순신")))
                .andExpect(jsonPath("$.data.applyTime", is("2024-10-01T12:00:00")));
    }

    /**
     * 유효한 요청으로 사용자 등록 내역을 조회하는 테스트
     */
    @Test
    @DisplayName("유효한 요청으로 사용자 등록 내역을 조회한다.")
    void searchRegistration_validRequest() throws Exception {
        // Given
        SearchRegistrationResponse mockResponse = SearchRegistrationResponse.builder()
                .userName("이순신")
                .appliedRegistrationResponseList(Arrays.asList(
                        new SearchRegistrationResponse.AppliedRegistrationResponse(
                                "테스트 강의",
                                "홍길동",
                                LocalDateTime.of(2024, 10, 5, 10, 0),
                                LocalDateTime.of(2024, 10, 1, 12, 0)
                        ),
                        new SearchRegistrationResponse.AppliedRegistrationResponse(
                                "테스트 강의",
                                "홍길동",
                                LocalDateTime.of(2024, 10, 6, 10, 0),
                                LocalDateTime.of(2024, 10, 2, 13, 0)
                        )
                ))
                .build();

        given(lectureFacade.findRegistrationByUser(1L)).willReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/lecture/registration")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.output", is(SUCCESS.getCode())))
                .andExpect(jsonPath("$.response.result", is(SUCCESS.getMessage())))
                .andExpect(jsonPath("$.data.userName", is("이순신")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList", hasSize(2)))
                // 첫 번째 등록 내역 검증
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[0].lectureName", is("테스트 강의")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[0].tutorName", is("홍길동")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[0].startTime", is("2024-10-05T10:00:00")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[0].applyTime", is("2024-10-01T12:00:00")))
                // 두 번째 등록 내역 검증
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[1].lectureName", is("테스트 강의")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[1].tutorName", is("홍길동")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[1].startTime", is("2024-10-06T10:00:00")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[1].applyTime", is("2024-10-02T13:00:00")));
    }

    @Test
    @DisplayName("잔여 좌석이 없을 때 강의 신청을 시도하면 예외가 발생한다.")
    void applyLecture_noRemainingCapacity_throwsException() throws Exception {
        // Given
        String requestBody = "{\"userId\":1,\"lectureItemId\":1000}";

        given(lectureFacade.applyLectureItem(any(ApplyLectureItemRequest.class)))
                .willThrow(new BusinessException(NO_REMAINING_REGISTRATION));

        // When & Then
        mockMvc.perform(post("/lecture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response.output", is(NO_REMAINING_REGISTRATION.getCode())))
                .andExpect(jsonPath("$.response.result", is(NO_REMAINING_REGISTRATION.getMessage())));
    }

    @Test
    @DisplayName("이미 신청한 강의에 대해 재신청을 시도하면 예외가 발생한다.")
    void applyLecture_alreadyApplied_throwsException() throws Exception {
        // Given
        String requestBody = "{\"userId\":1,\"lectureItemId\":1000}";

        given(lectureFacade.applyLectureItem(any(ApplyLectureItemRequest.class)))
                .willThrow(new BusinessException(ALREADY_APPLIED_LECTURE));

        // When & Then
        mockMvc.perform(post("/lecture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response.output", is(ALREADY_APPLIED_LECTURE.getCode())))
                .andExpect(jsonPath("$.response.result", is(ALREADY_APPLIED_LECTURE.getMessage())));
    }
}
