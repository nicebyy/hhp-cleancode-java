package hhplus.lecture.infrastructure;

import hhplus.lecture.domain.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureJpaRepository extends JpaRepository<Lecture,Long> {

}
