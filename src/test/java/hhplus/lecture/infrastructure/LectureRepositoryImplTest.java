package hhplus.lecture.infrastructure;

import hhplus.common.config.jpa.QueryDslConfig;
import hhplus.lecture.domain.entity.Lecture;
import hhplus.lecture.domain.entity.LectureItem;
import hhplus.lecture.domain.entity.LectureRegistration;
import hhplus.lecture.domain.repository.LectureRepository;
import hhplus.user.domain.entity.User;
import hhplus.user.infrastructure.UserJpaRepository;
import hhplus.user.infrastructure.UserRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({LectureRepositoryImpl.class, UserRepositoryImpl.class, QueryDslConfig.class})  // 필요한 리포지토리 구현체를 Import
@ActiveProfiles("test")  // 'test' 프로파일 활성화
class LectureRepositoryImplTest {

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LectureItemJpaRepository lectureItemJpaRepository;

    @Autowired
    private LectureRegistrationJpaRepository registrationJpaRepository;

    @Autowired
    private LectureJpaRepository lectureJpaRepository;

    @Autowired
    private EntityManager em;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    public void setUp(){
        lectureJpaRepository.deleteAll();
        registrationJpaRepository.deleteAll();
        lectureItemJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("강의 아이템 목록을 조회한다.")
    void findAllLectureItems() {
        // given
        Lecture lecture = new Lecture("테스트 강의", "홍길동");
        LocalDateTime now = LocalDateTime.now();
        Lecture saveedLecture = lectureJpaRepository.save(lecture);

        LectureItem lectureItem1 = new LectureItem(lecture, 30, LocalDateTime.now().plusDays(1));
        LectureItem lectureItem2 = new LectureItem(lecture, 30, LocalDateTime.now().plusDays(2));
        lectureItemJpaRepository.save(lectureItem1);
        lectureItemJpaRepository.save(lectureItem2);

//        // when
        List<LectureItem> result = lectureRepository.findAllLectureItems(null, now);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("lectureItemId")
                .containsExactlyInAnyOrder(lectureItem1.getLectureItemId(), lectureItem2.getLectureItemId());
    }

    @Test
    @DisplayName("특정 ID로 강의 아이템을 조회한다.")
    void findLectureItemById() {
        // given
        Lecture lecture = new Lecture("테스트 강의", "홍길동");
        lectureJpaRepository.save(lecture);

        LectureItem lectureItem = new LectureItem(lecture, 30, LocalDateTime.now().plusDays(1));
        lectureItemJpaRepository.save(lectureItem);

        // when
        Optional<LectureItem> result = lectureRepository.findLectureItemById(lectureItem.getLectureItemId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getLectureItemId()).isEqualTo(lectureItem.getLectureItemId());
    }

    @Test
    @DisplayName("사용자와 강의 아이템으로 강의 등록을 저장한다.")
    void saveLectureRegistration() {

        // given
        User user = new User("이순신");
        userJpaRepository.save(user);

        Lecture lecture = new Lecture("테스트 강의", "홍길동");
        lectureJpaRepository.save(lecture);

        LectureItem lectureItem = new LectureItem(lecture, 30, LocalDateTime.now().plusDays(1));
        lectureItemJpaRepository.save(lectureItem);

        // when
        LectureRegistration registration = lectureRepository.saveLectureRegistration(user, lectureItem);

        // then
        assertThat(registration).isNotNull();
        assertThat(registration.getUser().getUserId()).isEqualTo(user.getUserId());
        assertThat(registration.getLectureItem().getLectureItemId()).isEqualTo(lectureItem.getLectureItemId());
    }

    @Test
    @DisplayName("사용자와 강의로 등록 여부를 확인한다.")
    void checkRegistrationByUserAndLectureItem() {
        // given
        User user = new User("이순신");
        userJpaRepository.save(user);

        Lecture lecture = new Lecture("테스트 강의", "홍길동");
        lectureJpaRepository.save(lecture);

        LectureItem lectureItem = new LectureItem(lecture, 30, LocalDateTime.now().plusDays(1));
        lectureItemJpaRepository.save(lectureItem);

        LectureRegistration registration = new LectureRegistration(user, lectureItem);
        registrationJpaRepository.save(registration);

        // when
        boolean exists = lectureRepository.checkRegistrationByUserAndLectureItem(user, lecture);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("사용자의 모든 강의 등록 내역을 조회한다.")
    void findAllRegistrationByUser() {
        // given
        User user = new User("이순신");
        em.persist(user);

        Lecture lecture1 = new Lecture("테스트 강의1", "홍길동");
        Lecture lecture2 = new Lecture("테스트 강의2", "김철수");
        lectureJpaRepository.save(lecture1);
        lectureJpaRepository.save(lecture2);

        LectureItem lectureItem1 = new LectureItem(lecture1, 30, LocalDateTime.now().plusDays(1));
        LectureItem lectureItem2 = new LectureItem(lecture2, 30, LocalDateTime.now().plusDays(2));
        lectureItemJpaRepository.save(lectureItem1);
        lectureItemJpaRepository.save(lectureItem2);

        LectureRegistration registration1 = new LectureRegistration(user, lectureItem1);
        LectureRegistration registration2 = new LectureRegistration(user, lectureItem2);
        registration1 = registrationJpaRepository.save(registration1);
        registration2 = registrationJpaRepository.save(registration2);

        // when
        List<LectureRegistration> registrations = lectureRepository.findAllRegistrationByUser(user);

        // then
        assertThat(registrations).hasSize(2);
        assertThat(registrations).extracting("registrationId")
                .containsExactlyInAnyOrder(registration1.getRegistrationId(), registration2.getRegistrationId());
    }
}