package com.ormi.mogakcote.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ormi.mogakcote.user.domain.Authority;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAuthResponse {
    @JsonProperty("userId")
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String password;
    private Authority authority;

    public static UserAuthResponse toResponse(
            Long userId, String username, String nickname, String email, String password, Authority authority
    ) {
        return new UserAuthResponse(userId, username, nickname, email, password, authority);
    }
}
