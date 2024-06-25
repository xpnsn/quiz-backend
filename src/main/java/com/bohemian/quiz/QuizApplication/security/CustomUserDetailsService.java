package com.bohemian.quiz.QuizApplication.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.bohemian.quiz.QuizApplication.user.User;
import com.bohemian.quiz.QuizApplication.user.UserDao;

@Component
public class CustomUserDetailsService implements UserDetailsService{

    @Autowired
    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = userDao.findById(username)
        .orElseThrow(() -> new UsernameNotFoundException("Username not found : " + username));
        

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .build();
    }

}
