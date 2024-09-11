package com.ormi.mogakcote.profile.dto.request;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String username;
    private String nickname;
    private String email;
    private String password;
}
