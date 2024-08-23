package com.ormi.mogakcote.health.model.response;

import java.time.LocalDateTime;

public record HealthResponse(
        String env,
        LocalDateTime dateTime,
        String message
) {

    public static HealthResponse of(String env, String message) {
        return new HealthResponse(
                env,
                LocalDateTime.now(),
                message
        );
    }
}
