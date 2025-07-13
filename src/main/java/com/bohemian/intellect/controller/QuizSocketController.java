package com.bohemian.intellect.controller;

import com.bohemian.intellect.dto.*;
import com.bohemian.intellect.model.Question;
import com.bohemian.intellect.model.UserQuizSession;
import com.bohemian.intellect.service.AIService;
import com.bohemian.intellect.service.QuestionService;
import com.bohemian.intellect.service.QuizSocketService;
import com.bohemian.intellect.service.SessionManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class QuizSocketController {

    private final QuestionService questionService;
    private final SessionManager sessionManager;
    private final ObjectMapper mapper;
    private final AIService aiService;
    private final QuizSocketService quizSocketService;

    public QuizSocketController(QuestionService questionService, SessionManager sessionManager, ObjectMapper mapper, AIService aiService, QuizSocketService quizSocketService) {
        this.questionService = questionService;
        this.sessionManager = sessionManager;
        this.mapper = mapper;
        this.aiService = aiService;
        this.quizSocketService = quizSocketService;
    }

    @MessageMapping("/quiz/start")
    @SendToUser("/queue/questions")
    public QuestionDto handleQuizStart(QuizStartRequest req, Principal principal) {
        return quizSocketService.startQuiz(req, principal);
    }

    @MessageMapping("/ai/quiz/start")
    @SendToUser("/queue/questions")
    public QuestionDto handleAIQuizStart(AIQuizStartRequest req, Principal principal){

        String username = principal.getName();

        List<Question> questions = aiService.generateAnswers(
                Map.of(
                        "topic", req.topic(),
                        "size", req.size(),
                        "level", req.level()
                )
        );
        sessionManager.startSession(username, "ai-quiz", questions);

        return mapper.convertValue(questions.getFirst(), QuestionDto.class);
    }

    @MessageMapping("/quiz/answer")
    @SendToUser("queue/questions")
    public Object handleQuizAnswer(QuestionResponse response, Principal principal) {
        return quizSocketService.putAnswer(response, principal);
    }

}
