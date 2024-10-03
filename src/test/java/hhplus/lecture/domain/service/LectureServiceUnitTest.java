package hhplus.lecture.domain.service;

import hhplus.common.enums.ResponseCodeEnum;
import hhplus.common.exception.BusinessException;
import hhplus.lecture.domain.entity.Lecture;
import hhplus.lecture.domain.entity.LectureItem;
import hhplus.lecture.domain.entity.LectureRegistration;
import hhplus.lecture.domain.repository.LectureRepository;
import hhplus.lecture.presentation.dto.SearchLectureItemResponse;
import hhplus.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 *  Lecture Service 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class LectureServiceUnitTest {

    @InjectMocks
    private LectureService lectureService;

    @Mock
    private LectureRepository lectureRepository;

    @Test
    @DisplayName("특정 강의 ID와 날짜 조건으로 강의 아이템 목록을 성공적으로 조회한다.")
    void findAllLectureItems(){

        // given
        Long lectureId = 100L;
        String tutorName = "홍길동";
        String lectureName = "테스트강의1";
        LocalDateTime dateCond = LocalDateTime.now();
        LectureItem lectureItem1 = createMockLectureItem(lectureId, tutorName, lectureName, 1L, dateCond.plusDays(1));
        LectureItem lectureItem2 = createMockLectureItem(lectureId, tutorName, lectureName, 2L, dateCond.plusDays(2));

        List<LectureItem> lectureItems = Arrays.asList(lectureItem1, lectureItem2);

        given(lectureRepository.findAllLectureItems(lectureId, dateCond)).willReturn(lectureItems);

        // when
        List<LectureItem> result = lectureService.findAllLectureItems(lectureId, dateCond);

        // then
        then(lectureRepository).should().findAllLectureItems(lectureId, dateCond);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(lectureItem1, lectureItem2);
    }

    @Test
    @DisplayName("사용자가 강의 아이템에 성공적으로 신청한다.")
    void applyLectureItems(){

        // given
        Long userId = 1L;
        String userName = "이순신";
        User user = createMockUser(1L,userName);
        LectureItem lectureItem = createMockLectureItem(
                100L,
                "홍길동",
                "테스트강의1",
                1L,
                LocalDateTime.now().plusDays(1)
        );
        LectureRegistration expectedRegistration = new LectureRegistration(user, lectureItem);

        given(lectureRepository.saveLectureRegistration(user, lectureItem)).willReturn(expectedRegistration);

        // when
        LectureRegistration result = lectureService.applyLectureItems(user, lectureItem);

        // then
        then(lectureRepository).should().saveLectureRegistration(user, lectureItem);
        assertThat(result).isEqualTo(expectedRegistration);
    }

    @Test
    @DisplayName("강의 아이템 ID로 강의 아이템을 성공적으로 조회한다.")
    void findLectureItemById(){

        // given
        Long lectureItemId = 1L;
        LectureItem lectureItem = createMockLectureItem(
                100L,
                "홍길동",
                "테스트강의1",
                lectureItemId,
                LocalDateTime.now().plusDays(1)
        );

        given(lectureRepository.findLectureItemById(lectureItemId)).willReturn(Optional.of(lectureItem));

        // when
        LectureItem result = lectureService.findLectureItemById(lectureItemId);

        // then
        then(lectureRepository).should().findLectureItemById(lectureItemId);
        assertThat(result).isEqualTo(lectureItem);
    }


    @Test
    @DisplayName("강의 아이템 ID로 강의 아이템을 찾지 못하면 예외를 발생시킨다.")
    void findLectureItemByIdWithNotFount(){

        // given
        Long lectureItemId = 1L;

        given(lectureRepository.findLectureItemById(lectureItemId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> lectureService.findLectureItemById(lectureItemId)
        );

        then(lectureRepository).should().findLectureItemById(lectureItemId);
        assertThat(exception.getResponseCodeEnum()).isEqualTo(ResponseCodeEnum.LECTURE_NOT_FOUND);
    }

    @Test
    @DisplayName("사용자의 강의 등록 내역을 성공적으로 조회한다.")
    void findLectureRegistrationByUser(){

        // given
        Long userId = 1L;
        String userName = "이순신";
        User user = createMockUser(userId, userName);

        LectureItem lectureItem1 = createMockLectureItem(
                100L,
                "홍길동",
                "테스트강의1",
                1L,
                LocalDateTime.now().plusDays(1)
        );
        LectureItem lectureItem2 = createMockLectureItem(
                101L,
                "김철수",
                "테스트강의2",
                2L,
                LocalDateTime.now().plusDays(2)
        );

        LectureRegistration registration1 = new LectureRegistration(user, lectureItem1);
        LectureRegistration registration2 = new LectureRegistration(user, lectureItem2);

        List<LectureRegistration> registrations = Arrays.asList(registration1, registration2);

        given(lectureRepository.findAllRegistrationByUser(user)).willReturn(registrations);

        // when
        List<LectureRegistration> result = lectureService.findLectureRegistrationByUser(user);

        // then
        then(lectureRepository).should().findAllRegistrationByUser(user);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(registration1, registration2);
    }

    @Test
    @DisplayName("사용자의 강의 등록 내역이 없을 경우 예외를 발생시킨다.")
    void findLectureRegistrationByUserWithNoRegistrations(){

        // given
        Long userId = 1L;
        String userName = "이순신";
        User user = createMockUser(userId, userName);

        given(lectureRepository.findAllRegistrationByUser(user)).willReturn(Arrays.asList());

        // when & then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> lectureService.findLectureRegistrationByUser(user)
        );

        then(lectureRepository).should().findAllRegistrationByUser(user);
        assertThat(exception.getResponseCodeEnum()).isEqualTo(ResponseCodeEnum.NO_REGISTRATION);
    }

    @Test
    @DisplayName("사용자가 이미 해당 강의에 등록되어 있을 경우 true 를 반환한다.")
    void checkLectureAlreadyRegistration(){

        // given
        Long userId = 1L;
        String userName = "이순신";
        User user = createMockUser(userId, userName);

        LectureItem lectureItem = createMockLectureItem(
                100L,
                "홍길동",
                "테스트강의1",
                1L,
                LocalDateTime.now().plusDays(1)
        );
        Lecture lecture = lectureItem.getLecture();

        given(lectureRepository.checkRegistrationByUserAndLectureItem(user, lecture)).willReturn(true);

        // when
        boolean result = lectureService.checkLectureRegistration(user, lectureItem);

        // then
        then(lectureRepository).should().checkRegistrationByUserAndLectureItem(user, lecture);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("사용자가 해당 강의에 등록되어 있지 않을 경우 false 를 반환한다.")
    void checkLectureRegistration_NotRegistered(){

        // given
        Long userId = 1L;
        String userName = "이순신";
        User user = createMockUser(userId, userName);

        LectureItem lectureItem = createMockLectureItem(
                100L,
                "홍길동",
                "테스트강의1",
                1L,
                LocalDateTime.now().plusDays(1)
        );
        Lecture lecture = lectureItem.getLecture();

        given(lectureRepository.checkRegistrationByUserAndLectureItem(user, lecture)).willReturn(false);

        // when
        boolean result = lectureService.checkLectureRegistration(user, lectureItem);

        // then
        then(lectureRepository).should().checkRegistrationByUserAndLectureItem(user, lecture);
        assertThat(result).isFalse();
    }

    private LectureItem createMockLectureItem(Long lectureId,String tutorName, String lectureName, Long lectureItemId,LocalDateTime startTime) {
        Lecture lecture = new Lecture(lectureName, tutorName);
        LectureItem lectureItem = new LectureItem(lecture, 30, startTime);
        setField(lectureItem, "lectureItemId", lectureItemId);
        setField(lecture, "lectureId", lectureId);
        return lectureItem;
    }

    private User createMockUser(Long userId, String userName) {
        User user = new User(userName);
        setField(user, "userId", userId);
        return user;
    }
}