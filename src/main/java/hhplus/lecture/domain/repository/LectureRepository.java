package hhplus.lecture.domain.repository;

import hhplus.lecture.domain.entity.Lecture;
import hhplus.lecture.domain.entity.LectureItem;
import hhplus.lecture.domain.entity.LectureRegistration;
import hhplus.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LectureRepository {
    public List<LectureItem> findAllLectureItems(Long lectureId, LocalDateTime dateCond);

    public Optional<LectureItem> findLectureItemById(Long lectureId);

    LectureRegistration saveLectureRegistration(User user, LectureItem lectureItem);

    boolean checkRegistrationByUserAndLectureItem(User user, Lecture lecture);

    public List<LectureRegistration> findAllRegistrationByUser(User user);
}
