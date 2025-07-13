package com.bohemian.intellect.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String resultID;
    private String quizId;
    private String username;
    private int totalQuestions;
    private int correctAnswers;

    private List<String> answers;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

}
