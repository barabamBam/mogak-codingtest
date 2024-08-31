package com.ormi.mogakcote.user.infrastructure;

import com.ormi.mogakcote.user.domain.Authority;
import com.ormi.mogakcote.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u.email FROM User u WHERE u.email = :email AND u.nickname = :nickname")
    Optional<String> findEmailByNameAndNickname(String email, String nickname);

    Optional<User> findByAuthority(Authority authority);
  
    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);
}
