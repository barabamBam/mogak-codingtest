package com.ormi.mogakcote.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Column(name = "join_at")
    @CreatedDate
    private LocalDateTime joinAt;

    @Embedded private Activity activity;

    public void updateAuth(Authority authority) {

        this.authority = authority;
    }

    public void updateProfile(String name, String nickname, String email) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
  
    public void updateEmail(String email) {
        this.email = email;
    }
}
