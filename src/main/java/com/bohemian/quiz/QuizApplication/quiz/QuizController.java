package com.bohemian.quiz.QuizApplication.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quiz")
public class QuizController {

    @Autowired
    QuizService quizService;

    @GetMapping("all")
    public ResponseEntity<?> getAllQuiz() {
        return new ResponseEntity<>(quizService.getAllQuiz(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> saveQuiz(@RequestBody Quiz quiz){
        if(quizService.getQuizByUUID(quiz.getUuid()) != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        quizService.saveNewQuiz(quiz);
        return new ResponseEntity<>(quiz, HttpStatus.CREATED);
    }

    @DeleteMapping("{uuid}")
    public ResponseEntity<?> deleteQuiz(@PathVariable String uuid) {
        quizService.deleteQuiz(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping()
    public ResponseEntity<?> getQuizByUsername() {
        List<Quiz> quizzes = quizService.getQuizByUsername();
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

}
