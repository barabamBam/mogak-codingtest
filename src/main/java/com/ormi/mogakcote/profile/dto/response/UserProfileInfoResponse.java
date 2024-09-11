package com.ormi.mogakcote.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileInfoResponse {
    private String username;
    private String nickname;
    private String email;
    private String password;

    public static UserProfileInfoResponse toResponse(
        String username,
        String nickname,
        String email,
        String password
    ) {
        return new UserProfileInfoResponse(username, nickname, email, password);
    }
}
