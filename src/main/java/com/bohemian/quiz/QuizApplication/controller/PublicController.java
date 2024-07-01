package com.bohemian.quiz.QuizApplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bohemian.quiz.QuizApplication.user.User;
import com.bohemian.quiz.QuizApplication.user.UserService;

@RestController
public class PublicController {

    @Autowired
    UserService userService;

    @PostMapping("sign-up")
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        if(userService.getUserByUsername(user.getUsername()) == null)  {
            userService.saveNewUser(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }



    @GetMapping("test")
    public String test() {
        return "SUCCESS";
    }
}
