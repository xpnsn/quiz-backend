package com.bohemian.intellect.service;

import com.bohemian.intellect.dto.QuestionDto;
import com.bohemian.intellect.dto.QuestionResponse;
import com.bohemian.intellect.model.QuizResult;
import com.bohemian.intellect.dto.QuizStartRequest;
import com.bohemian.intellect.model.Question;
import com.bohemian.intellect.model.UserQuizSession;
import com.bohemian.intellect.repository.ResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class QuizSocketService {

    private final QuestionService questionService;
    private final SessionManager sessionManager;
    private final ObjectMapper mapper;
    private final ResultRepository resultRepository;
    private final UserService userService;

    public QuizSocketService(QuestionService questionService, SessionManager sessionManager, ObjectMapper mapper, ResultRepository resultRepository, UserService userService) {
        this.questionService = questionService;
        this.sessionManager = sessionManager;
        this.mapper = mapper;
        this.resultRepository = resultRepository;
        this.userService = userService;
    }

    public QuestionDto startQuiz(QuizStartRequest req, Principal principal) {
        String username = principal.getName();

        List<Question> questions = questionService.getQuestionFromQuiz(req.quizId());
        sessionManager.startSession(username, req.quizId(), questions);

        return mapper.convertValue(questions.getFirst(), QuestionDto.class);
    }

    public Object putAnswer(QuestionResponse response, Principal principal) {
        String username = principal.getName();

        UserQuizSession session = sessionManager.getSession(username);

        if(session.putAnswer(response.answer())) {
            return session.getCurrentQuestion();
        } else {
            QuizResult quizResult = session.evaluateResult();
            quizResult = resultRepository.save(quizResult);
            userService.addQuizToUser(quizResult.getQuizId());
            sessionManager.removeSession(username);
            return quizResult;
        }
    }
}
