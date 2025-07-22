package com.example.trivia.service;

import com.example.trivia.dto.GameSessionSummaryDto;
import com.example.trivia.dto.QuestionDto;
import com.example.trivia.dto.TriviaQuestionResponse;
import com.example.trivia.dto.TriviaTokenResponse;
import com.example.trivia.entity.GameSession;
import com.example.trivia.entity.User;
import com.example.trivia.repository.GameSessionRepository;
import com.example.trivia.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@Service
public class GameSessionService {

    private static final Logger log = LoggerFactory.getLogger(GameSessionService.class);

    @Value("${opentdb.base-url}")
    private String triviaApiBaseUrl;

    private final GameSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private final Map<Long, String> currentCorrectAnswers = new HashMap<>();

    public GameSessionService(GameSessionRepository sessionRepository,
                              UserRepository userRepository,
                              RestTemplate restTemplate) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public GameSession getSession(Long sessionId) {
        log.info("Fetching session with ID: {}", sessionId);
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found with ID: " + sessionId));
    }

    public GameSession startSoloArcadeSession(Long userId, List<Integer> categories) {
        log.info("Starting solo arcade session for user ID: {}, categories: {}", userId, categories);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));

        String tokenUrl = triviaApiBaseUrl + "/api_token.php?command=request";
        TriviaTokenResponse tokenResponse = restTemplate.getForObject(tokenUrl, TriviaTokenResponse.class);

        if (tokenResponse == null || tokenResponse.getToken() == null) {
            throw new IllegalStateException("Failed to fetch token from Trivia API");
        }

        GameSession session = new GameSession();
        session.setHost(user);
        session.setMultiplayer(false);
        session.setArcadeMode(true);
        session.setStarted(true);
        session.setEnded(false);
        session.setScore(0);
        session.setStartedAt(Instant.now());
        session.setToken(tokenResponse.getToken());
        session.setCategoryFilter(categories != null ? categories : new ArrayList<>());

        return sessionRepository.save(session);
    }

    public QuestionDto fetchNextQuestion(Long sessionId) {
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found with ID: " + sessionId));

        if (session.isEnded()) {
            throw new IllegalStateException("Session has ended");
        }

        log.info("Fetching question for session ID: {}", sessionId);

        StringBuilder url = new StringBuilder(triviaApiBaseUrl + "/api.php?amount=1&token=" + session.getToken());
        if (!session.getCategoryFilter().isEmpty()) {
            url.append("&category=").append(session.getCategoryFilter().get(0));
        }

        TriviaQuestionResponse response = restTemplate.getForObject(url.toString(), TriviaQuestionResponse.class);

        if (response == null || response.getResults().isEmpty()) {
            throw new IllegalStateException("Failed to fetch question from Trivia API");
        }

        QuestionDto question = response.getResults().get(0);
        currentCorrectAnswers.put(sessionId, question.getCorrectAnswer());

        return question;
    }

    public boolean submitAnswer(Long sessionId, String userAnswer) {
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found with ID: " + sessionId));

        String correctAnswer = currentCorrectAnswers.get(sessionId);
        if (correctAnswer == null) {
            throw new IllegalStateException("No question fetched yet for this session");
        }

        log.info("Session {}: received answer '{}'", sessionId, userAnswer);

        boolean correct = correctAnswer.equalsIgnoreCase(userAnswer.trim());

        if (correct) {
            session.setScore(session.getScore() + 1);
        } else {
            session.setEnded(true);
            session.setEndedAt(Instant.now());
        }

        sessionRepository.save(session);
        return correct;
    }

    public List<GameSessionSummaryDto> getSessionsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        List<GameSession> sessions = sessionRepository.findByHostOrderByStartedAtDesc(user);

        return sessions.stream().map(session -> {
            GameSessionSummaryDto dto = new GameSessionSummaryDto();
            dto.setSessionId(session.getId());
            dto.setMultiplayer(session.isMultiplayer());
            dto.setArcadeMode(session.isArcadeMode());
            dto.setEnded(session.isEnded());
            dto.setScore(session.getScore());
            dto.setStartedAt(session.getStartedAt());
            dto.setEndedAt(session.getEndedAt());
            dto.setCategoryFilter(session.getCategoryFilter());
            return dto;
        }).toList();
    }

}
