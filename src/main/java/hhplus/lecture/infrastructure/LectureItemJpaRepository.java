package hhplus.lecture.infrastructure;

import hhplus.lecture.domain.entity.LectureItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface LectureItemJpaRepository extends JpaRepository<LectureItem,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Optional<LectureItem> findByLectureItemId(Long lectureItemId);
}
