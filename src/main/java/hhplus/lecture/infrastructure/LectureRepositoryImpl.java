package hhplus.lecture.infrastructure;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hhplus.lecture.domain.entity.Lecture;
import hhplus.lecture.domain.entity.LectureItem;
import hhplus.lecture.domain.entity.LectureRegistration;
import hhplus.lecture.domain.entity.QLectureItem;
import hhplus.lecture.domain.repository.LectureRepository;
import hhplus.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureRepository{

    private final LectureItemJpaRepository lectureItemJpaRepository;
    private final LectureRegistrationJpaRepository registrationJpaRepository;
    private final JPAQueryFactory query;

    @Override
    public List<LectureItem> findAllLectureItems(Long lectureId, LocalDateTime dateCond) {

        QLectureItem lectureItem = QLectureItem.lectureItem;

        return query
                .selectFrom(lectureItem)
                .join(lectureItem.lecture).fetchJoin()
                .where(
                        searchLectureByIdCond(lectureId),
                        searchLectureByDateCond(dateCond),
                        lectureItem.currentCapacity.loe(lectureItem.totalCapacity)
                )
                .orderBy(lectureItem.startTime.desc())
                .fetch();
    }

    @Override
    public Optional<LectureItem> findLectureItemById(Long lectureId) {
        return lectureItemJpaRepository.findByLectureItemId(lectureId);
    }

    @Override
    public LectureRegistration saveLectureRegistration(User user, LectureItem lectureItem) {
        return registrationJpaRepository.save(new LectureRegistration(user, lectureItem));
    }

    @Override
    public boolean checkRegistrationByUserAndLectureItem(User user, Lecture lecture) {
        return registrationJpaRepository.existsByUserAndLectureItem_Lecture(user, lecture);
    }

    @Override
    public List<LectureRegistration> findAllRegistrationByUser(User user) {
        return registrationJpaRepository.findAllByUser(user);
    }

    private BooleanExpression searchLectureByIdCond(Long lectureId){
        BooleanExpression cond = null;
        if(!ObjectUtils.isEmpty(lectureId)){
            cond = QLectureItem.lectureItem.lecture.lectureId.eq(lectureId);
        }
        return cond;
    }

    private BooleanExpression searchLectureByDateCond(LocalDateTime time){
        BooleanExpression cond = null;
        if(!ObjectUtils.isEmpty(time)){
            cond = QLectureItem.lectureItem.startTime.goe(time);
        }
        return cond;
    }
}
