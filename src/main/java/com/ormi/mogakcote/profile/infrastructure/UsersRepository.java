package com.ormi.mogakcote.profile.infrastructure;

import com.ormi.mogakcote.profile.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByNickname(String nickname);
}