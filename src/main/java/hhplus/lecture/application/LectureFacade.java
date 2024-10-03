package hhplus.lecture.application;

import hhplus.common.enums.ResponseCodeEnum;
import hhplus.common.exception.BusinessException;
import hhplus.lecture.domain.entity.LectureItem;
import hhplus.lecture.domain.entity.LectureRegistration;
import hhplus.lecture.domain.service.LectureService;
import hhplus.lecture.presentation.dto.*;
import hhplus.user.domain.entity.User;
import hhplus.user.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class LectureFacade {

    private final UserService userService;
    private final LectureService lectureService;

    public Map<LocalDateTime, List<SearchLectureItemResponse>> findLectureItems(SearchLectureItemRequest dto){

        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonth().getValue();
        int day = now.getDayOfMonth();

        if(dto.getYear() != null){
            year = dto.getYear();
        }
        if(dto.getMonth() != null){
            month = dto.getMonth();
        }
        if(dto.getDay() != null){
            day = dto.getDay();
        }
        return lectureService.findAllLectureItems(dto.getLectureId(), LocalDate.of(year, month, day).atStartOfDay());
    }

    @Transactional
    public ApplyLectureItemResponse applyLectureItem(ApplyLectureItemRequest dto){

        User user = userService.findUserById(dto.getUserId());
        LectureItem lectureItem = lectureService.findLectureItemById(dto.getLectureItemId());

        checkRemainLecture(lectureItem);
        checkDuplicationApply(user, lectureItem);
        LectureRegistration lectureRegistration = lectureService.applyLectureItems(user, lectureItem);

        return ApplyLectureItemResponse.builder()
                .lectureName(lectureItem.getLecture().getLectureName())
                .applyTime(lectureRegistration.getCreateDate())
                .tutorName(lectureItem.getLecture().getTutorName())
                .userName(user.getUserName())
                .build();
    }

    private void checkRemainLecture(LectureItem lectureItem){
        if(lectureItem.getTotalCapacity()<=lectureItem.getCurrentCapacity()){
            throw new BusinessException(ResponseCodeEnum.NO_REMAINING_REGISTRATION);
        }
    }

    private void checkDuplicationApply(User user, LectureItem lectureItem){
        if(lectureService.checkLectureRegistration(user, lectureItem)){
            throw new BusinessException(ResponseCodeEnum.ALREADY_APPLIED_LECTURE);
        }
    }
    public SearchRegistrationResponse findRegistrationByUser(Long userId){

        User user = userService.findUserById(userId);
        List<LectureRegistration> registrationList = lectureService.findLectureRegistrationByUser(user);

        return SearchRegistrationResponse.builder()
                .userName(user.getUserName())
                .appliedRegistrationResponseList(
                        registrationList.stream()
                                .map(registration-> new SearchRegistrationResponse.AppliedRegistrationResponse(
                                            registration.getLectureItem().getLecture().getLectureName(),
                                            registration.getLectureItem().getLecture().getTutorName(),
                                            registration.getLectureItem().getStartTime(),
                                            registration.getCreateDate()
                                )).collect(Collectors.toList())
                ).build();
    }
}
