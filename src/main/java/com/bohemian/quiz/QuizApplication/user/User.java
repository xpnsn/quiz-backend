package com.bohemian.quiz.QuizApplication.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "user_table")
public class User {

    @Id
    private String username;
    private String password;
    private int score;
}
