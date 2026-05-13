package com.boogieton.nadok.domain.user.repository;

import com.boogieton.nadok.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
