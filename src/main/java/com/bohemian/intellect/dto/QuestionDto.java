package com.bohemian.intellect.dto;

import java.util.List;

public record QuestionDto(
        String title,
        List<String> options
) {

}
