package com.bohemian.intellect.dto;

public record LoginRequest(
        String username,
        String password
) {
}
