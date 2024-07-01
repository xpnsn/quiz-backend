package com.bohemian.quiz.QuizApplication.quiz;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizDao extends JpaRepository<Quiz, String> {

    List<Quiz> findAllByUsername(String username);
}
