package hhplus.lecture.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hhplus.lecture.domain.entity.Lecture;
import hhplus.lecture.domain.entity.QLecture;
import hhplus.lecture.domain.repository.LectureRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureRepository{

    private final LectureJpaRepository lectureJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Lecture> findAll() {
        return List.of();
    }
}
