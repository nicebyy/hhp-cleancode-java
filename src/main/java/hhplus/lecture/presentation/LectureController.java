package hhplus.lecture.presentation;

import hhplus.common.api.ApiResponse;
import hhplus.lecture.application.LectureFacade;
import hhplus.lecture.domain.entity.LectureItem;
import hhplus.lecture.presentation.dto.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequestMapping("/lecture")
@RestController
@RequiredArgsConstructor
@Slf4j
public class LectureController {

    private final LectureFacade lectureFacade;

    @GetMapping
    public ResponseEntity<ApiResponse> searchLectureItemSearch(
            @ModelAttribute SearchLectureItemRequest dto
    ){
        Map<LocalDateTime, List<SearchLectureItemResponse>> response = lectureFacade.findLectureItems(dto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> applyLecture(
            @RequestBody ApplyLectureItemRequest dto
    ){
        ApplyLectureItemResponse response = lectureFacade.applyLectureItem(dto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/registration")
    public ResponseEntity<ApiResponse> searchRegistration(
            @RequestParam("userId") Long userId
    ){
        SearchRegistrationResponse response = lectureFacade.findRegistrationByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
