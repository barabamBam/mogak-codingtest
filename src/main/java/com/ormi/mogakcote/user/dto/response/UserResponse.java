package com.ormi.mogakcote.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    @JsonProperty("userId")
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String password;


    public static UserResponse toResponse(
            Long userId, String username, String nickname, String email, String password
            ) {
        return new UserResponse(userId, username, nickname, email, password);
    }
}