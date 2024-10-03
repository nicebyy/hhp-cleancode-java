package hhplus.lecture.domain.application;


import hhplus.common.exception.BusinessException;
import hhplus.lecture.application.LectureFacade;
import hhplus.lecture.domain.entity.Lecture;
import hhplus.lecture.domain.entity.LectureItem;
import hhplus.lecture.infrastructure.LectureItemJpaRepository;
import hhplus.lecture.infrastructure.LectureJpaRepository;
import hhplus.lecture.infrastructure.LectureRegistrationJpaRepository;
import hhplus.lecture.presentation.dto.ApplyLectureItemRequest;
import hhplus.lecture.presentation.dto.ApplyLectureItemResponse;
import hhplus.user.domain.entity.User;
import hhplus.user.infrastructure.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static hhplus.common.enums.ResponseCodeEnum.NO_REMAINING_REGISTRATION;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class LectureConcurrencyTest {

    @Autowired
    LectureFacade lectureFacade;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    LectureJpaRepository lectureJpaRepository;

    @Autowired
    LectureItemJpaRepository lectureItemJpaRepository;

    @Autowired
    LectureRegistrationJpaRepository registrationJpaRepository;


    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();
        registrationJpaRepository.deleteAll();
        lectureItemJpaRepository.deleteAll();
        lectureJpaRepository.deleteAll();
    }

    /**
     * 한명이 동시에 5번 신청하면 한번만 성공한다.
     */
    @Test()
    void concurrencyTestWithUserInOneLectureWithManyTry() throws InterruptedException {

        User user = new User("유저1");
        userJpaRepository.save(user);

        Lecture lecture = new Lecture("강의", "강사");
        lectureJpaRepository.save(lecture);

        LectureItem lectureItem = new LectureItem(lecture, 30, LocalDateTime.now().plusDays(1));
        lectureItemJpaRepository.save(lectureItem);

        ApplyLectureItemRequest applyLectureItemRequest = new ApplyLectureItemRequest(user.getUserId(), lectureItem.getLectureItemId());

        int numberOfThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        // when
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger otherErrorCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    ApplyLectureItemResponse response = lectureFacade.applyLectureItem(applyLectureItemRequest);
                    successCount.incrementAndGet();
                } catch (BusinessException ex) {
                    if (ex.getResponseCodeEnum() == NO_REMAINING_REGISTRATION) {
                        failureCount.incrementAndGet();
                    } else {
                        otherErrorCount.incrementAndGet();
                        log.error("Unexpected BusinessException: {}", ex.getMessage());
                    }
                } catch (Exception e) {
                    otherErrorCount.incrementAndGet();
                    log.error("Unexpected Exception: ", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        boolean finished = executorService.awaitTermination(1, TimeUnit.MINUTES);
        if (!finished) {
            executorService.shutdownNow();
        }

        // Then
        int expectedSuccess = 1;
        log.info("success: {}", successCount.get());
        assertEquals(expectedSuccess, successCount.get());
    }

    /**
     * 50명이 동시에 하나의 강의에 신청하면 30명만 성공한다.
     */
    @Test()
    void concurrencyTestWithManyUserInOneLecture() throws InterruptedException {

        List<User> userList = new ArrayList<User>();
        for (int i = 0; i < 50; i++) {
            userList.add(new User("user" + i));
        }
        userJpaRepository.saveAll(userList);

        Lecture lecture = new Lecture("강의", "강사");
        lectureJpaRepository.save(lecture);

        LectureItem lectureItem = new LectureItem(lecture, 30, LocalDateTime.now().plusDays(1));
        lectureItemJpaRepository.save(lectureItem);

        List<ApplyLectureItemRequest> requestList = userList.stream()
                .map(user -> new ApplyLectureItemRequest(user.getUserId(), lectureItem.getLectureItemId()))
                .toList();

        int numberOfThreads = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(50);

        // when
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger otherErrorCount = new AtomicInteger(0);

        for (ApplyLectureItemRequest applyLectureItemRequest : requestList) {

            executorService.execute(() -> {
                try {
                    ApplyLectureItemResponse response = lectureFacade.applyLectureItem(applyLectureItemRequest);
                    successCount.incrementAndGet();
                } catch (BusinessException ex) {
                    if (ex.getResponseCodeEnum() == NO_REMAINING_REGISTRATION) {
                        failureCount.incrementAndGet();
                    } else {
                        otherErrorCount.incrementAndGet();
                        log.error("Unexpected BusinessException: {}", ex.getMessage());
                    }
                } catch (Exception e) {
                    otherErrorCount.incrementAndGet();
                    log.error("Unexpected Exception: ", e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        boolean finished = executorService.awaitTermination(1, TimeUnit.MINUTES);
        if (!finished) {
            executorService.shutdownNow();
        }

        // Then
        int expectedSuccess = 30;
        log.info("success: {}", successCount.get());
        assertEquals(expectedSuccess, successCount.get());
    }
}
