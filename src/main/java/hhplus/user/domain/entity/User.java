package hhplus.user.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User{

    @Id @GeneratedValue
    private Long userId;

    private String userName;

    public User(String userName){
        this.userName = userName;
    }
}
