package com.bohemian.intellect.dto;

public record RegistrationRequest (
        String username,
        String password,
        String name,
        String email
){
}
