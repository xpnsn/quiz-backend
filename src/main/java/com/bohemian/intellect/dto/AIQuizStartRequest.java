package com.bohemian.intellect.dto;

public record AIQuizStartRequest(
        String topic,
        String size,
        String level
) {
}
