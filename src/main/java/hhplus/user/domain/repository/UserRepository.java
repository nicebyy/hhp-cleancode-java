package hhplus.user.domain.repository;

import hhplus.user.domain.entity.User;

import java.util.Optional;

public interface UserRepository {

    public Optional<User> findById(Long userId);
}
