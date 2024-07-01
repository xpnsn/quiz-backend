package com.bohemian.quiz.QuizApplication.question;

import com.bohemian.quiz.QuizApplication.quiz.Quiz;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "question_table")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String questionTitle;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String correctAnswer;
    private String quizUuid;
}
