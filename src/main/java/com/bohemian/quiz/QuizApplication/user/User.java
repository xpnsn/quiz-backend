package com.bohemian.quiz.QuizApplication.user;

import com.bohemian.quiz.QuizApplication.quiz.Quiz;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Entity
@Table(name = "user_table")
public class User {

    @Id
    private String username;
    private String name;
    private String password;
    @OneToMany(mappedBy = "username")
    private List<Quiz> quiz;
}
