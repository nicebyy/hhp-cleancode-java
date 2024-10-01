package hhplus.lecture.domain.repository;

import hhplus.lecture.domain.entity.Lecture;

import java.util.List;

public interface LectureRepository {

    public List<Lecture> findAll();
}
