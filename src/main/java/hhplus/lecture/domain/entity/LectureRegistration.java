package hhplus.lecture.domain.entity;

import hhplus.common.config.jpa.BaseTime;
import hhplus.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureRegistration extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long registrationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_item_id")
    private LectureItem lectureItem;

    public LectureRegistration(User user, LectureItem lectureItem){
        this.user = user;
        this.lectureItem = lectureItem;
        this.lectureItem.addCurrentCapacity();
    }
}
