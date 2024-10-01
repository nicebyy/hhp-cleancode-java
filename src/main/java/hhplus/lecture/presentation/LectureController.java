package hhplus.lecture.presentation;

import hhplus.lecture.application.LectureFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LectureController {

    private final LectureFacade lectureFacade;


}
