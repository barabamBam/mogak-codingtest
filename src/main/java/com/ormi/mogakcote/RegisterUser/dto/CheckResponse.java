package com.ormi.mogakcote.RegisterUser.dto;

public class CheckResponse {
    private boolean available;

    public CheckResponse(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}