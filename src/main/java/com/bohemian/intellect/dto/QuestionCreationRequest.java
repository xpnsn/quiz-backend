package com.bohemian.intellect.dto;

import java.util.List;

public record QuestionCreationRequest(
        String title,
        List<String> options,
        String correctAnswer,
        String quizId
) {
}
