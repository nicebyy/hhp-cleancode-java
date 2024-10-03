package hhplus.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    private String userName;

    public User(String userName){
        this.userName = userName;
    }
}
