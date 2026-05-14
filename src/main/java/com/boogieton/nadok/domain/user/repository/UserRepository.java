package com.boogieton.nadok.domain.user.repository;

import com.boogieton.nadok.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);
    public User findByNickname(String nickname);
    public Boolean existsByNickname(String nickname);
    public Boolean existsByEmail(String email);
}
