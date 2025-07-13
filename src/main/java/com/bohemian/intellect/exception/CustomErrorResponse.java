package com.bohemian.intellect.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record CustomErrorResponse(
        String title,
        int status,
        String timeStamp,
        Map<String, String> errors
) {}
