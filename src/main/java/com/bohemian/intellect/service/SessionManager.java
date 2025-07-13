package com.bohemian.intellect.service;

import com.bohemian.intellect.model.Question;
import com.bohemian.intellect.model.UserQuizSession;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final Map<String, UserQuizSession> sessions = new ConcurrentHashMap<>();

    public void startSession(String username, String quizId, List<Question> questions) {
        sessions.put(username, new UserQuizSession(username, quizId, questions, LocalDateTime.now()));
    }

    public UserQuizSession getSession(String username) {
        return sessions.get(username);
    }

    public void removeSession(String username) {
        sessions.remove(username);
    }
}
