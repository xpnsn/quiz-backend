package com.bohemian.quiz.QuizApplication.quiz;


import com.bohemian.quiz.QuizApplication.question.Question;
import com.bohemian.quiz.QuizApplication.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "quiz_table")
public class Quiz {

    @Id
    private String uuid = UUID.randomUUID().toString().substring(0,6);
    private String name;
    private String description;
    private String username;
    @OneToMany(mappedBy = "quizUuid")
    private List<Question> questions;
}
