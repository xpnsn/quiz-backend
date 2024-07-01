package com.bohemian.quiz.QuizApplication.question;

import com.bohemian.quiz.QuizApplication.quiz.Quiz;
import com.bohemian.quiz.QuizApplication.quiz.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    QuizService quizService;

    public List<Question> getAllQuestions() {
        return questionDao.findAll();
    }

    public boolean saveNewQuestion(Question question, String uuid) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        question.setQuizUuid(uuid);
        Quiz quiz = quizService.getQuizByUUID(uuid);
        if(quiz.getUsername().equals(username)) {
            questionDao.save(question);
            return true;
        }
        return false;
    }

    public Question getQuestionById(int id) {
        return questionDao.findById(id).orElse(null);
    }

    public boolean deleteQuestionById(int id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Question question = questionDao.findById(id).orElse(null);
        String quizUsername = quizService.getQuizByUUID(question.getQuizUuid()).getUsername();
        if(username.equals(quizUsername)) {
            questionDao.deleteById(id);
            return true;
        }
        return false;
    }
}
