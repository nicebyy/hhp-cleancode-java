package hhplus.lecture.infrastructure;

import hhplus.lecture.domain.entity.Lecture;
import hhplus.lecture.domain.entity.LectureRegistration;
import hhplus.user.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureRegistrationJpaRepository extends JpaRepository<LectureRegistration,Long> {

    @EntityGraph(attributePaths = {"user","lectureItem","lectureItem.lecture"})
    List<LectureRegistration> findAllByUser(User user);

    @EntityGraph(attributePaths = {"user","lectureItem","lectureItem.lecture"})
    boolean existsByUserAndLectureItem_Lecture(User user, Lecture lecture);
}
