package com.bohemian.quiz.QuizApplication.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User getUserByUsername(String username) {
        return userDao.findById(username).orElse(null);
    }

    public void saveNewUser(User user) {
        user.setPassword(passwordEncoder().encode(user.getPassword()));
        userDao.save(user);
    }

    public void deleteUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userDao.deleteById(username);
    }

    // public void updateUser(User user) {
    //     String username = SecurityContextHolder.getContext().getAuthentication().getName();
    //     User oldUser = userDao.findById(username).orElse(null);
    //     String newUsername = user.getUsername();
    //     String newPassword = user.getPassword();
    //     oldUser.setUsername(newUsername != "" ? newUsername : oldUser.getUsername());
    //     oldUser.setPassword(newPassword != "" ? newPassword : oldUser.getPassword());

    //     userDao.save(oldUser);
    // }

}
