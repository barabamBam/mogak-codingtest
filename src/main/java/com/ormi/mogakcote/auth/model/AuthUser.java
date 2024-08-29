package com.ormi.mogakcote.auth.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthUser {

    @NotNull
    private final Long id;
}