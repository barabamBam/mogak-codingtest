package com.ormi.mogakcote.user.dto.request;

public class UpdateProfileRequest {
    private String username;  // name 대신 username 사용
    private String nickname;

    // getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

