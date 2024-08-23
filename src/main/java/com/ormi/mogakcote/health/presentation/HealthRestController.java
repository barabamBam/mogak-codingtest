package com.ormi.mogakcote.health.presentation;

import com.ormi.mogakcote.health.model.response.HealthResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthRestController {

    private final Environment environment;

    @GetMapping
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(
                HealthResponse.of(
                        Arrays.toString(environment.getActiveProfiles()),
                        "health Good~!"
                ));

    }
}