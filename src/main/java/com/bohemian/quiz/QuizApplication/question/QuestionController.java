package com.bohemian.quiz.QuizApplication.question;

import com.bohemian.quiz.QuizApplication.quiz.Quiz;
import com.bohemian.quiz.QuizApplication.quiz.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Autowired
    QuizService quizService;

    @GetMapping
    public List<Question> getAllQuestion() {
        return questionService.getAllQuestions();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable int id) {
        Question question = questionService.getQuestionById(id);
        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    @PostMapping("{uuid}")
    public ResponseEntity<?> saveQuestion(@RequestBody Question question, @PathVariable String uuid) {
        if(questionService.saveNewQuestion(question, uuid)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable int id) {
        if(questionService.getQuestionById(id) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(questionService.deleteQuestionById(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
