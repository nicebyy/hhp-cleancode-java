package hhplus.user.domain.service;

import hhplus.common.enums.ResponseCodeEnum;
import hhplus.common.exception.BusinessException;
import hhplus.user.domain.entity.User;
import hhplus.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(ResponseCodeEnum.USER_NOT_FOUND));
    }
}
