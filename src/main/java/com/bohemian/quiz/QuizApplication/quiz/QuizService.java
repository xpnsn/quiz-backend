package com.bohemian.quiz.QuizApplication.quiz;

import com.bohemian.quiz.QuizApplication.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;
    @Autowired
    UserDao userDao;

    public List<Quiz> getAllQuiz() {
        return quizDao.findAll();
    }

    public Quiz getQuizByUUID(String uuid) {
        return quizDao.findById(uuid).orElse(null);
    }

    public void saveNewQuiz(Quiz quiz) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        quiz.setUsername(username);
        quiz.setQuestions(Collections.emptyList());
        quizDao.save(quiz);
    }

    public void deleteQuiz(String uuid) {
        quizDao.deleteById(uuid);
    }

    public List<Quiz> getQuizByUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return quizDao.findAllByUsername(username);
    }
}
