package com.bohemian.intellect.dto;

import java.util.Map;

public record EmailSenderDto(
        String to,
        String type,
        String subject,
        Map<String, String> data
) {
}
