package hhplus.lecture.domain.service;

import hhplus.common.enums.ResponseCodeEnum;
import hhplus.common.exception.BusinessException;
import hhplus.lecture.domain.entity.LectureItem;
import hhplus.lecture.domain.entity.LectureRegistration;
import hhplus.lecture.domain.repository.LectureRepository;
import hhplus.lecture.presentation.dto.SearchLectureItemResponse;
import hhplus.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;

    public Map<LocalDateTime, List<SearchLectureItemResponse>> findAllLectureItems(Long lectureId, LocalDateTime dateCond) {

        return lectureRepository.findAllLectureItems(lectureId, dateCond).stream()
                .map(lectureItem -> {
                    return SearchLectureItemResponse.builder()
                            .lectureId(lectureItem.getLecture().getLectureId())
                            .lectureName(lectureItem.getLecture().getLectureName())
                            .tutorName(lectureItem.getLecture().getTutorName())
                            .startTime(lectureItem.getStartTime())
                            .totalCapacity(lectureItem.getTotalCapacity())
                            .currentCapacity(lectureItem.getCurrentCapacity())
                            .build();
                })
                .collect(
                        groupingBy(
                                SearchLectureItemResponse::getStartTime,
                                TreeMap::new, toList()
                        )
                );
    }

    @Transactional
    public LectureRegistration applyLectureItems(User user, LectureItem lectureItem){
        return lectureRepository.saveLectureRegistration(user,lectureItem);
    }

    public LectureItem findLectureItemById(Long lectureItemId){
        return lectureRepository.findLectureItemById(lectureItemId)
                .orElseThrow(() -> new BusinessException(ResponseCodeEnum.LECTURE_NOT_FOUND));
    }

    public List<LectureRegistration> findLectureRegistrationByUser(User user){
        List<LectureRegistration> allRegistrationByUser = lectureRepository.findAllRegistrationByUser(user);
        if(allRegistrationByUser.isEmpty()){
            throw new BusinessException(ResponseCodeEnum.NO_REGISTRATION);
        }
        return allRegistrationByUser;
    }

    public boolean checkLectureRegistration(User user, LectureItem lectureItem){
        return lectureRepository.checkRegistrationByUserAndLectureItem(user, lectureItem.getLecture());
    }
}
