package hhplus.lecture.domain.application;

import hhplus.common.enums.ResponseCodeEnum;
import hhplus.common.exception.BusinessException;
import hhplus.lecture.application.LectureFacade;
import hhplus.lecture.domain.entity.Lecture;
import hhplus.lecture.domain.entity.LectureItem;
import hhplus.lecture.domain.entity.LectureRegistration;
import hhplus.lecture.domain.service.LectureService;
import hhplus.lecture.presentation.dto.*;
import hhplus.user.domain.entity.User;
import hhplus.user.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.util.ReflectionTestUtils.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class LectureFacadeUnitTest {

    @InjectMocks
    private LectureFacade lectureFacade;

    @Mock
    private UserService userService;

    @Mock
    private LectureService lectureService;

    private User mockUser;
    private Lecture mockLecture;
    private LectureItem mockLectureItem1;
    private LectureItem mockLectureItem2;
    private LectureRegistration mockRegistration1;
    private LectureRegistration mockRegistration2;

    @BeforeEach
    void setUp() {
        // 공통적으로 사용될 모킹 객체 설정
        mockUser = new User("이순신");
        setField(mockUser, "userId", 1L);

        mockLecture = new Lecture("테스트 강의", "홍길동");
        setField(mockLecture, "lectureId", 100L);

        mockLectureItem1 = new LectureItem(mockLecture, 30, LocalDateTime.of(2024, 10, 5, 10, 0));
        setField(mockLectureItem1, "lectureItemId", 1000L);
        setField(mockLectureItem1, "currentCapacity", 10);

        mockLectureItem2 = new LectureItem(mockLecture, 30, LocalDateTime.of(2024, 10, 6, 10, 0));
        setField(mockLectureItem2, "lectureItemId", 1001L);
        setField(mockLectureItem2, "currentCapacity", 20);

        mockRegistration1 = new LectureRegistration(mockUser, mockLectureItem1);
        setField(mockRegistration1, "registrationId", 5000L);
        setField(mockRegistration1, "createDate", LocalDateTime.of(2024, 10, 1, 12, 0));

        mockRegistration2 = new LectureRegistration(mockUser, mockLectureItem2);
        setField(mockRegistration2, "registrationId", 5001L);
        setField(mockRegistration2, "createDate", LocalDateTime.of(2024, 10, 2, 13, 0));
    }

    @Test
    @DisplayName("강의 아이템 목록을 정상적으로 조회한다.")
    void findLectureItems_success() {
        // Given
        SearchLectureItemRequest request = new SearchLectureItemRequest();
        request.setLectureId(100L);
        request.setYear(2024);
        request.setMonth(10);
        request.setDay(4);

        LocalDateTime dateCond = LocalDateTime.of(2024, 10, 4, 0, 0);

        List<LectureItem> lectureItems = Arrays.asList(mockLectureItem1, mockLectureItem2);
        given(lectureService.findAllLectureItems(100L, dateCond)).willReturn(lectureItems);

        // When
        Map<LocalDateTime, List<SearchLectureItemResponse>> result = lectureFacade.findLectureItems(request);

        // Then
        then(lectureService).should().findAllLectureItems(100L, dateCond);
        assertThat(result).hasSize(2);
        assertThat(result).containsKeys(
                LocalDateTime.of(2024, 10, 5, 10, 0),
                LocalDateTime.of(2024, 10, 6, 10, 0)
        );

        List<SearchLectureItemResponse> responsesDay1 = result.get(LocalDateTime.of(2024, 10, 5, 10, 0));
        List<SearchLectureItemResponse> responsesDay2 = result.get(LocalDateTime.of(2024, 10, 6, 10, 0));

        assertThat(responsesDay1).hasSize(1);
        assertThat(responsesDay2).hasSize(1);

        SearchLectureItemResponse response1 = responsesDay1.get(0);
        SearchLectureItemResponse response2 = responsesDay2.get(0);

        assertThat(response1.getLectureId()).isEqualTo(100L);
        assertThat(response1.getLectureName()).isEqualTo("테스트 강의");
        assertThat(response1.getTutorName()).isEqualTo("홍길동");
        assertThat(response1.getStartTime()).isEqualTo(LocalDateTime.of(2024, 10, 5, 10, 0));
        assertThat(response1.getTotalCapacity()).isEqualTo(30);
        assertThat(response1.getCurrentCapacity()).isEqualTo(11);

        assertThat(response2.getLectureId()).isEqualTo(100L);
        assertThat(response2.getLectureName()).isEqualTo("테스트 강의");
        assertThat(response2.getTutorName()).isEqualTo("홍길동");
        assertThat(response2.getStartTime()).isEqualTo(LocalDateTime.of(2024, 10, 6, 10, 0));
        assertThat(response2.getTotalCapacity()).isEqualTo(30);
        assertThat(response2.getCurrentCapacity()).isEqualTo(21);
    }

    @Test
    @DisplayName("강의 등록을 성공적으로 수행한다.")
    void applyLectureItem_success() {
        // Given
        ApplyLectureItemRequest request = new ApplyLectureItemRequest();
        request.setUserId(1L);
        request.setLectureItemId(1000L);

        given(userService.findUserById(1L)).willReturn(mockUser);
        given(lectureService.findLectureItemById(1000L)).willReturn(mockLectureItem1);
        given(lectureService.checkLectureRegistration(mockUser, mockLectureItem1)).willReturn(false);
        given(lectureService.applyLectureItems(mockUser, mockLectureItem1)).willReturn(mockRegistration1);

        // When
        ApplyLectureItemResponse response = lectureFacade.applyLectureItem(request);

        // Then
        then(userService).should().findUserById(1L);
        then(lectureService).should().findLectureItemById(1000L);
        then(lectureService).should().checkLectureRegistration(mockUser, mockLectureItem1);
        then(lectureService).should().applyLectureItems(mockUser, mockLectureItem1);

        assertThat(response).isNotNull();
        assertThat(response.getLectureName()).isEqualTo("테스트 강의");
        assertThat(response.getTutorName()).isEqualTo("홍길동");
        assertThat(response.getUserName()).isEqualTo("이순신");
        assertThat(response.getApplyTime()).isEqualTo(mockRegistration1.getCreateDate());
    }

    @Test
    @DisplayName("잔여 인원이 없을 때 강의 등록을 시도하면 예외가 발생한다.")
    void applyLectureItem_noRemainingCapacity_throwsException() {
        // Given
        ApplyLectureItemRequest request = new ApplyLectureItemRequest();
        request.setUserId(1L);
        request.setLectureItemId(1000L);

        // 잔여 인원이 없도록 설정
        setField(mockLectureItem1, "currentCapacity", 30); // totalCapacity = 30, currentCapacity = 30

        given(userService.findUserById(1L)).willReturn(mockUser);
        given(lectureService.findLectureItemById(1000L)).willReturn(mockLectureItem1);

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> lectureFacade.applyLectureItem(request)
        );

        then(userService).should().findUserById(1L);
        then(lectureService).should().findLectureItemById(1000L);
        then(lectureService).should(never()).checkLectureRegistration(any(User.class), any(LectureItem.class));
        then(lectureService).should(never()).applyLectureItems(any(User.class), any(LectureItem.class));

        assertThat(exception.getResponseCodeEnum()).isEqualTo(ResponseCodeEnum.NO_REMAINING_REGISTRATION);
    }

    @Test
    @DisplayName("이미 등록된 사용자가 강의 등록을 시도하면 예외가 발생한다.")
    void applyLectureItem_alreadyApplied_throwsException() {
        // Given
        ApplyLectureItemRequest request = new ApplyLectureItemRequest();
        request.setUserId(1L);
        request.setLectureItemId(1000L);

        given(userService.findUserById(1L)).willReturn(mockUser);
        given(lectureService.findLectureItemById(1000L)).willReturn(mockLectureItem1);
        given(lectureService.checkLectureRegistration(mockUser, mockLectureItem1)).willReturn(true);

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> lectureFacade.applyLectureItem(request)
        );

        then(userService).should().findUserById(1L);
        then(lectureService).should().findLectureItemById(1000L);
        then(lectureService).should().checkLectureRegistration(mockUser, mockLectureItem1);
        then(lectureService).should(never()).applyLectureItems(any(User.class), any(LectureItem.class));

        assertThat(exception.getResponseCodeEnum()).isEqualTo(ResponseCodeEnum.ALREADY_APPLIED_LECTURE);
    }

    @Test
    @DisplayName("사용자의 모든 강의 등록 내역을 정상적으로 조회한다.")
    void findRegistrationByUser_success() {
        // Given
        Long userId = 1L;

        List<LectureRegistration> registrations = Arrays.asList(mockRegistration1, mockRegistration2);
        given(userService.findUserById(userId)).willReturn(mockUser);
        given(lectureService.findLectureRegistrationByUser(mockUser)).willReturn(registrations);

        // When
        SearchRegistrationResponse response = lectureFacade.findRegistrationByUser(userId);

        // Then
        then(userService).should().findUserById(userId);
        then(lectureService).should().findLectureRegistrationByUser(mockUser);

        assertThat(response).isNotNull();
        assertThat(response.getUserName()).isEqualTo("이순신");
        assertThat(response.getAppliedRegistrationResponseList()).hasSize(2);

        SearchRegistrationResponse.AppliedRegistrationResponse resp1 = response.getAppliedRegistrationResponseList().get(0);
        SearchRegistrationResponse.AppliedRegistrationResponse resp2 = response.getAppliedRegistrationResponseList().get(1);

        assertThat(resp1.getLectureName()).isEqualTo("테스트 강의");
        assertThat(resp1.getTutorName()).isEqualTo("홍길동");
        assertThat(resp1.getStartTime()).isEqualTo(LocalDateTime.of(2024, 10, 5, 10, 0));
        assertThat(resp1.getApplyTime()).isEqualTo(LocalDateTime.of(2024, 10, 1, 12, 0));

        assertThat(resp2.getLectureName()).isEqualTo("테스트 강의");
        assertThat(resp2.getTutorName()).isEqualTo("홍길동");
        assertThat(resp2.getStartTime()).isEqualTo(LocalDateTime.of(2024, 10, 6, 10, 0));
        assertThat(resp2.getApplyTime()).isEqualTo(LocalDateTime.of(2024, 10, 2, 13, 0));
    }
}