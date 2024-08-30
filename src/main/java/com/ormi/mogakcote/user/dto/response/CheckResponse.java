package com.ormi.mogakcote.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckResponse {
    private boolean available;

//    public CheckResponse(boolean available) {
//        this.available = available;
//    }

    public static CheckResponse toResponse(boolean available) {
        return new CheckResponse(available);
    }

//    public boolean isAvailable() {
//        return available;
//    }
//
//    public void setAvailable(boolean available) {
//        this.available = available;
//    }
}