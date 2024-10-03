package hhplus;

import hhplus.common.enums.ResponseCodeEnum;
import hhplus.lecture.domain.entity.Lecture;
import hhplus.lecture.domain.entity.LectureItem;
import hhplus.lecture.domain.entity.LectureRegistration;
import hhplus.lecture.domain.service.LectureService;
import hhplus.lecture.infrastructure.LectureItemJpaRepository;
import hhplus.lecture.infrastructure.LectureJpaRepository;
import hhplus.lecture.infrastructure.LectureRegistrationJpaRepository;
import hhplus.user.domain.entity.User;
import hhplus.user.domain.repository.UserRepository;
import hhplus.user.infrastructure.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
class LectureApiE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LectureItemJpaRepository lectureItemJpaRepository;

    @Autowired
    private LectureRegistrationJpaRepository lectureRegistrationJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private LectureJpaRepository lectureJpaRepository;

    @Autowired
    private LectureService lectureService;

    private User testUser;
    private Lecture testLecture;
    private LectureItem lectureItem1;
    private LectureItem lectureItem2;

    @BeforeEach
    void setUp() {
        // 모든 리포지토리를 초기화합니다.
        lectureRegistrationJpaRepository.deleteAll();
        lectureItemJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
        lectureJpaRepository.deleteAll();

        // 테스트 유저 생성
        testUser = new User("이순신");
        testUser = userJpaRepository.save(testUser);

        // 테스트 강의 생성
        testLecture = new Lecture("테스트 강의", "홍길동");
        testLecture = lectureJpaRepository.save(testLecture);

        // 테스트 강의 아이템 생성
        lectureItem1 = new LectureItem(testLecture, 30, LocalDateTime.of(2024, 10, 5, 10, 0));
        lectureItem1 = lectureItemJpaRepository.save(lectureItem1);

        lectureItem2 = new LectureItem(testLecture, 30, LocalDateTime.of(2024, 10, 6, 10, 0));
        lectureItem2 = lectureItemJpaRepository.save(lectureItem2);

        lectureService.applyLectureItems(testUser,lectureItem1);
        lectureService.applyLectureItems(testUser,lectureItem2);
    }

    /**
     * 유효한 요청으로 강의 아이템 목록을 조회하는 E2E 테스트
     */
    @Test
    @DisplayName("E2E 테스트: 유효한 요청으로 강의 아이템 목록을 조회한다.")
    void searchLectureItemSearch_E2E_validRequest() throws Exception {

        mockMvc.perform(get("/lecture"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.output", is(ResponseCodeEnum.SUCCESS.getCode())))
                .andExpect(jsonPath("$.response.result", is(ResponseCodeEnum.SUCCESS.getMessage())))
                .andExpect(jsonPath("$.data.*", hasSize(2))); // 두 개의 날짜별 강의 아이템
    }

    /**
     * 유효하지 않은 요청 - year가 최소값보다 작을 때
     */
    @Test
    @DisplayName("E2E 테스트: year가 최소값보다 작을 때 유효성 검증 실패")
    void searchLectureItemSearch_E2E_invalidYearTooSmall() throws Exception {
        mockMvc.perform(get("/lecture")
                        .param("lectureId", String.valueOf(testLecture.getLectureId()))
                        .param("year", "2023") // 최소값 2024
                        .param("month", "10")
                        .param("day", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response.output", is(ResponseCodeEnum.VALIDATION_ERROR.getCode())))
                .andExpect(jsonPath("$.response.result").value(ResponseCodeEnum.VALIDATION_ERROR.getMessage()));
    }

    /**
     * 유효하지 않은 요청 - month가 최대값보다 클 때
     */
    @Test
    @DisplayName("E2E 테스트: month가 최대값보다 클 때 유효성 검증 실패")
    void searchLectureItemSearch_E2E_invalidMonthTooLarge() throws Exception {
        mockMvc.perform(get("/lecture")
                        .param("lectureId", String.valueOf(testLecture.getLectureId()))
                        .param("year", "2024")
                        .param("month", "13") // 최대값 12
                        .param("day", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response.output", is(ResponseCodeEnum.VALIDATION_ERROR.getCode())))
                .andExpect(jsonPath("$.response.result").value(ResponseCodeEnum.VALIDATION_ERROR.getMessage()));
    }

    /**
     * 유효하지 않은 요청 - day가 최소값보다 작을 때
     */
    @Test
    @DisplayName("E2E 테스트: day가 최소값보다 작을 때 유효성 검증 실패")
    void searchLectureItemSearch_E2E_invalidDayTooSmall() throws Exception {
        mockMvc.perform(get("/lecture")
                        .param("lectureId", String.valueOf(testLecture.getLectureId()))
                        .param("year", "2024")
                        .param("month", "10")
                        .param("day", "0")) // 최소값 1
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response.output", is(ResponseCodeEnum.VALIDATION_ERROR.getCode())))
                .andExpect(jsonPath("$.response.result").value(ResponseCodeEnum.VALIDATION_ERROR.getMessage()));
    }

    /**
     * 유효하지 않은 요청 - day가 최대값보다 클 때
     */
    @Test
    @DisplayName("E2E 테스트: day가 최대값보다 클 때 유효성 검증 실패")
    void searchLectureItemSearch_E2E_invalidDayTooLarge() throws Exception {
        mockMvc.perform(get("/lecture")
                        .param("lectureId", String.valueOf(testLecture.getLectureId()))
                        .param("year", "2024")
                        .param("month", "10")
                        .param("day", "32")) // 최대값 31
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response.output", is(ResponseCodeEnum.VALIDATION_ERROR.getCode())))
                .andExpect(jsonPath("$.response.result").value(ResponseCodeEnum.VALIDATION_ERROR.getMessage()));
    }

    /**
     * 유효한 요청으로 강의 신청을 하는 E2E 테스트
     */
    @Test
    @DisplayName("E2E 테스트: 유효한 요청으로 강의 신청을 한다.")
    void applyLecture_E2E_validRequest() throws Exception {

        // Given
        Lecture newLecture = new Lecture("테스트 강의2", "박지성");
        newLecture = lectureJpaRepository.save(newLecture);

        LectureItem newLectureItem = new LectureItem(newLecture, 30, LocalDateTime.of(2024, 10, 5, 10, 0));
        newLectureItem = lectureItemJpaRepository.save(newLectureItem);

        String requestBody = "{\"userId\":" + testUser.getUserId() + ",\"lectureItemId\":" + newLectureItem.getLectureItemId() + "}";

        // When & Then
        mockMvc.perform(post("/lecture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.output", is(ResponseCodeEnum.SUCCESS.getCode())))
                .andExpect(jsonPath("$.response.result", is(ResponseCodeEnum.SUCCESS.getMessage())))
                .andExpect(jsonPath("$.data.lectureName", is("테스트 강의2")))
                .andExpect(jsonPath("$.data.tutorName", is("박지성")))
                .andExpect(jsonPath("$.data.userName", is("이순신")))
                .andExpect(jsonPath("$.data.applyTime").exists());
    }

    /**
     * 유효하지 않은 요청 - lectureItemId가 누락되었을 때 E2E 테스트
     */
    @Test
    @DisplayName("E2E 테스트: lectureItemId가 누락되었을 때 유효성 검증 실패")
    void applyLecture_E2E_missingLectureItemId() throws Exception {
        String requestBody = "{\"userId\":" + testUser.getUserId() + "}";

        mockMvc.perform(post("/lecture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response.output", is(ResponseCodeEnum.VALIDATION_ERROR.getCode())))
                .andExpect(jsonPath("$.response.result").value(ResponseCodeEnum.VALIDATION_ERROR.getMessage()));
    }

    /**
     * 유효한 요청으로 사용자 등록 내역을 조회하는 E2E 테스트
     */
    @Test
    @DisplayName("E2E 테스트: 유효한 요청으로 사용자 등록 내역을 조회한다.")
    void searchRegistration_E2E_validRequest() throws Exception {

        mockMvc.perform(get("/lecture/registration")
                        .param("userId", String.valueOf(testUser.getUserId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.output", is(ResponseCodeEnum.SUCCESS.getCode())))
                .andExpect(jsonPath("$.response.result", is(ResponseCodeEnum.SUCCESS.getMessage())))
                .andExpect(jsonPath("$.data.userName", is("이순신")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList", hasSize(2)))
                // 첫 번째 등록 내역 검증
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[0].lectureName", is("테스트 강의")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[0].tutorName", is("홍길동")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[0].startTime", is("2024-10-05T10:00:00")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[0].applyTime").exists())
                // 두 번째 등록 내역 검증
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[1].lectureName", is("테스트 강의")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[1].tutorName", is("홍길동")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[1].startTime", is("2024-10-06T10:00:00")))
                .andExpect(jsonPath("$.data.appliedRegistrationResponseList[1].applyTime").exists());
    }

    /**
     * 유효하지 않은 요청 - userId가 누락되었을 때 E2E 테스트
     */
    @Test
    @DisplayName("E2E 테스트: userId가 누락되었을 때 특강 신청시 유효성 검증 실패")
    void searchRegistration_E2E_missingUserId() throws Exception {
        mockMvc.perform(get("/lecture/registration"))
                .andExpect(jsonPath("$.response.output", is(ResponseCodeEnum.VALIDATION_ERROR.getCode())))
                .andExpect(jsonPath("$.response.result").value(ResponseCodeEnum.VALIDATION_ERROR.getMessage()));
    }

    /**
     * E2E 테스트: 잔여 좌석이 없을 때 강의 신청 시도
     */
    @Test
    @DisplayName("E2E 테스트: 잔여 좌석이 없을 때 강의 신청 시도 시 유효성 검증 실패")
    void applyLecture_E2E_noRemainingCapacity() throws Exception {

        // Given
        Lecture newLecture = new Lecture("테스트 강의2", "박지성");
        newLecture = lectureJpaRepository.save(newLecture);

        LectureItem newLectureItem = new LectureItem(newLecture, 0, LocalDateTime.of(2024, 10, 5, 10, 0));
        newLectureItem = lectureItemJpaRepository.save(newLectureItem);

        String requestBody = "{\"userId\":" + testUser.getUserId() + ",\"lectureItemId\":" + newLectureItem.getLectureItemId() + "}";

        // When & Then
        mockMvc.perform(post("/lecture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response.output", is(ResponseCodeEnum.NO_REMAINING_REGISTRATION.getCode())))
                .andExpect(jsonPath("$.response.result", is(ResponseCodeEnum.NO_REMAINING_REGISTRATION.getMessage())));
    }

    /**
     * E2E 테스트: 이미 신청한 강의를 다시 신청 시도
     */
    @Test
    @DisplayName("E2E 테스트: 이미 신청한 강의를 다시 신청 시도 시 유효성 검증 실패")
    void applyLecture_E2E_alreadyAppliedLecture() throws Exception {

        // Given
        // 이미 신청한 강의 아이템에 대해 등록 내역 생성
        LectureRegistration existingRegistration = new LectureRegistration(testUser, lectureItem1);
        lectureRegistrationJpaRepository.save(existingRegistration);

        String requestBody = "{\"userId\":" + testUser.getUserId() + ",\"lectureItemId\":" + lectureItem1.getLectureItemId() + "}";

        // When & Then
        mockMvc.perform(post("/lecture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response.output", is(ResponseCodeEnum.ALREADY_APPLIED_LECTURE.getCode())))
                .andExpect(jsonPath("$.response.result", is(ResponseCodeEnum.ALREADY_APPLIED_LECTURE.getMessage())));
    }
}
