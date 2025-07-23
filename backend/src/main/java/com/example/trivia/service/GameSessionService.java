package com.example.trivia.service;

import com.example.trivia.dto.*;
import com.example.trivia.entity.GamePlayer;
import com.example.trivia.entity.GameSession;
import com.example.trivia.entity.User;
import com.example.trivia.repository.GamePlayerRepository;
import com.example.trivia.repository.GameSessionRepository;
import com.example.trivia.repository.UserRepository;
import com.example.trivia.websocket.QuestionBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GameSessionService {

    private static final Logger log = LoggerFactory.getLogger(GameSessionService.class);

    @Value("${opentdb.base-url}")
    private String triviaApiBaseUrl;

    private final GameSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final GamePlayerRepository gamePlayerRepository;
    private final QuestionBroadcaster broadcaster;

    public GameSessionService(GameSessionRepository sessionRepository,
                              UserRepository userRepository,
                              RestTemplate restTemplate,
                              GamePlayerRepository gamePlayerRepository,
                              QuestionBroadcaster broadcaster) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.gamePlayerRepository = gamePlayerRepository;
        this.broadcaster = broadcaster;
    }

    public GameSession getSession(Long sessionId) {
        log.info("Fetching session with ID: {}", sessionId);
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found with ID: " + sessionId));
    }

    private String fetchTriviaToken() {
        String tokenUrl = triviaApiBaseUrl + "/api_token.php?command=request";
        TriviaTokenResponse tokenResponse = restTemplate.getForObject(tokenUrl, TriviaTokenResponse.class);

        if (tokenResponse == null || tokenResponse.getToken() == null) {
            throw new IllegalStateException("Failed to retrieve trivia token");
        }

        return tokenResponse.getToken();
    }

    public GameSession startSoloArcadeSession(Long userId, List<Integer> categories, String difficulty) {
        User user = userRepository.findById(userId).orElse(null);

        String token = fetchTriviaToken();

        GameSession session = new GameSession();
        session.setHost(user);
        session.setMultiplayer(false);
        session.setArcadeMode(true);
        session.setStarted(true);
        session.setEnded(false);
        session.setScore(0);
        session.setStartedAt(Instant.now());
        session.setToken(token);
        session.setCategoryFilter(categories != null ? categories : new ArrayList<>());
        session.setDifficulty(difficulty);

        return sessionRepository.save(session);
    }

    public GameSession startClassicSession(Long userId, List<Integer> categories, String difficulty, Integer questionLimit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = fetchTriviaToken();

        GameSession session = new GameSession();
        session.setHost(user);
        session.setMultiplayer(false);
        session.setArcadeMode(false);
        session.setClassicMode(true);
        session.setStarted(true);
        session.setEnded(false);
        session.setScore(0);
        session.setStartedAt(Instant.now());
        session.setToken(token);
        session.setCategoryFilter(categories != null ? categories : new ArrayList<>());
        session.setDifficulty(difficulty != null ? difficulty : "");
        session.setQuestionLimit(questionLimit != null ? questionLimit : 10);
        session.setQuestionsAnswered(0);

        return sessionRepository.save(session);
    }

    public GameSession createMultiplayerSession(Long hostUserId, boolean arcadeMode, boolean classicMode,
                                                Integer questionLimit, String difficulty, List<Integer> categories) {
        User host = userRepository.findById(hostUserId).orElseThrow(() -> new IllegalArgumentException("Host not found"));

        String token = fetchTriviaToken();

        GameSession session = new GameSession();
        session.setHost(host);
        session.setArcadeMode(arcadeMode);
        session.setClassicMode(classicMode);
        session.setQuestionLimit(classicMode ? (questionLimit != null ? questionLimit : 10) : 0);
        session.setDifficulty(difficulty);
        session.setMultiplayer(true);
        session.setStarted(false);
        session.setEnded(false);
        session.setScore(0);
        session.setToken(token);
        session.setCategoryFilter(categories != null ? categories : new ArrayList<>());
        session.setStartedAt(null);
        session.setCurrentQuestionNumber(0);
        session.setQuestionsAnswered(0);

        session = sessionRepository.save(session);

        GamePlayer player = new GamePlayer();
        player.setGameSession(session);
        player.setUser(host);
        player.setHost(true);
        player.setReady(false);
        player.setScore(0);
        gamePlayerRepository.save(player);

        return session;
    }

    public void startMultiplayerGame(Long sessionId) {
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.isStarted()) {
            throw new IllegalStateException("Session already started");
        }

        boolean allReady = session.getPlayers().stream().allMatch(GamePlayer::isReady);
        if (!allReady) {
            throw new IllegalStateException("Not all players are ready");
        }

        session.setStarted(true);
        session.setStartedAt(Instant.now());
        session.setCurrentQuestionNumber(1);
        sessionRepository.save(session);

        fetchQuestionMultiplayer(sessionId);
    }

    public GamePlayer joinMultiplayerSession(Long userId, Long sessionId) {
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.isStarted()) {
            throw new IllegalStateException("Cannot join a session that has already started");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean alreadyJoined = session.getPlayers().stream()
                .anyMatch(player -> player.getUser().getId().equals(userId));

        if (alreadyJoined) {
            throw new IllegalStateException("User already joined");
        }

        GamePlayer player = new GamePlayer();
        player.setGameSession(session);
        player.setUser(user);
        player.setHost(false);
        player.setReady(false);
        player.setScore(0);
        return gamePlayerRepository.save(player);
    }

    public void setPlayerReady(Long sessionId, Long userId, boolean ready) {
        GamePlayer player = gamePlayerRepository.findByGameSessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found in session"));

        player.setReady(ready);
        gamePlayerRepository.save(player);
    }

    public void fetchQuestionMultiplayer(Long sessionId) {
        GameSession session = sessionRepository.findById(sessionId).orElseThrow();

        if (session.isEnded() || !session.isStarted()) {
            throw new IllegalStateException("Session is not active");
        }

        StringBuilder url = new StringBuilder(triviaApiBaseUrl + "/api.php?amount=1&token=" + session.getToken());
        if (!session.getCategoryFilter().isEmpty()) {
            Random randomNumbers = new Random();
            url.append("&category=").append(session.getCategoryFilter().get(randomNumbers.nextInt(session.getCategoryFilter().size())));
        }
        if (session.getDifficulty() != null) {
            url.append("&difficulty=").append(session.getDifficulty());
        }

        TriviaQuestionResponse response = restTemplate.getForObject(url.toString(), TriviaQuestionResponse.class);
        QuestionDto question = response.getResults().get(0);
        session.setCurrentCorrectAnswer(question.getCorrectAnswer());
        session.setCurrentQuestionNumber(session.getCurrentQuestionNumber() + 1);
        sessionRepository.save(session);


        MultiplayerQuestionBroadcast broadcast = new MultiplayerQuestionBroadcast();
        broadcast.setSessionId(sessionId);
        broadcast.setQuestionText(question.getQuestion());
        broadcast.setAnswers(Stream.concat(question.getIncorrectAnswers().stream(), Stream.of(question.getCorrectAnswer()))
                .sorted()
                .toArray(String[]::new));
        broadcaster.broadcastQuestion(sessionId, broadcast);

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
            Random randomNumbers = new Random();
            url.append("&category=").append(session.getCategoryFilter().get(randomNumbers.nextInt(session.getCategoryFilter().size())));
        }
        if (session.getDifficulty() != null && !session.getDifficulty().isBlank()) {
            url.append("&difficulty=").append(session.getDifficulty());
        }

        TriviaQuestionResponse response = restTemplate.getForObject(url.toString(), TriviaQuestionResponse.class);

        if (response == null || response.getResults().isEmpty()) {
            throw new IllegalStateException("Failed to fetch question from Trivia API");
        }

        QuestionDto question = response.getResults().get(0);
        session.setCurrentCorrectAnswer(question.getCorrectAnswer());
        sessionRepository.save(session);

        return question;
    }

    public SoloGameAnswerResultDto submitAnswer(Long sessionId, String userAnswer) {
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        String correctAnswer = session.getCurrentCorrectAnswer();
        if (correctAnswer == null) throw new IllegalStateException("No question fetched yet");

        boolean correct = correctAnswer.equalsIgnoreCase(userAnswer.trim());

        if (correct) {
            session.setScore(session.getScore() + 1);
        }

        session.setQuestionsAnswered(session.getQuestionsAnswered() + 1);

        boolean ended = false;

        if (session.isArcadeMode() && !correct) {
            session.setEnded(true);
            session.setEndedAt(Instant.now());
            ended = true;
        }

        if (session.isClassicMode() && session.getQuestionsAnswered() >= session.getQuestionLimit()) {
            session.setEnded(true);
            session.setEndedAt(Instant.now());
            ended = true;
        }

        sessionRepository.save(session);

        SoloGameAnswerResultDto result = new SoloGameAnswerResultDto();
        result.setCorrect(correct);
        result.setEnded(ended);
        result.setMode(session.isArcadeMode() ? "arcade" : "classic");
        result.setScore(session.getScore());
        result.setQuestionsAnswered(session.getQuestionsAnswered());
        result.setQuestionLimit(session.isClassicMode() ? session.getQuestionLimit() : 0);

        return result;
    }

    public MultiplayerGameResultDto submitAnswerMultiplayer(Long sessionId, Long userId, String userAnswer) {
        GameSession session = sessionRepository.findById(sessionId).orElseThrow();
        GamePlayer player = gamePlayerRepository.findByGameSessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found in session"));

        if (player.getAnsweredQuestions() >= session.getCurrentQuestionNumber()) {
            throw new IllegalStateException("Player has already answered this question");
        }

        String correctAnswer = session.getCurrentCorrectAnswer();
        if (correctAnswer == null) throw new IllegalStateException("No question fetched yet");

        boolean correct = correctAnswer.equalsIgnoreCase(userAnswer.trim());

        if (session.isArcadeMode()) {
            if (correct) {
                player.setScore(player.getScore() + 1);
            } else {
                player.setEliminated(true);
            }

            player.setAnsweredQuestions(session.getCurrentQuestionNumber());
            gamePlayerRepository.save(player);

            boolean allEliminated = session.getPlayers().stream().allMatch(GamePlayer::isEliminated);
            if (allEliminated) {
                session.setEnded(true);
                session.setEndedAt(Instant.now());
                sessionRepository.save(session);
            } else {
                long playersRemaining = session.getPlayers().stream().filter(p -> !p.isEliminated()).count();
                long playersAnswered = session.getPlayers().stream()
                        .filter(p -> !p.isEliminated() && p.getAnsweredQuestions() >= session.getCurrentQuestionNumber())
                        .count();

                if (playersRemaining > 0 && playersAnswered == playersRemaining) {
                    session.setQuestionsAnswered(session.getQuestionsAnswered() + 1);
                    sessionRepository.save(session);
                    fetchQuestionMultiplayer(sessionId);
                }
            }
        } else if (session.isClassicMode()) {
            player.setScore(correct ? player.getScore() + 1 : player.getScore());
            player.setAnsweredQuestions(session.getCurrentQuestionNumber());
            gamePlayerRepository.save(player);

            long playersAnswered = session.getPlayers().stream()
                    .filter(p -> p.getAnsweredQuestions() >= session.getCurrentQuestionNumber())
                    .count();

            if (playersAnswered == session.getPlayers().size()) {
                session.setQuestionsAnswered(session.getQuestionsAnswered() + 1);

                if (session.getQuestionsAnswered() >= session.getQuestionLimit()) {
                    session.setEnded(true);
                    session.setEndedAt(Instant.now());
                    sessionRepository.save(session);
                } else {
                    fetchQuestionMultiplayer(sessionId);
                }
            }
        }

        MultiplayerGameResultDto result = new MultiplayerGameResultDto();
        result.setCorrect(correct);
        result.setGameEnded(session.isEnded());
        result.setPlayerScore(player.getScore());

        List<PlayerStatusDto> players = session.getPlayers().stream().map(p -> {
            PlayerStatusDto dto = new PlayerStatusDto();
            dto.setUserId(p.getUser().getId());
            dto.setUsername(p.getUser().getUsername());
            dto.setScore(p.getScore());
            dto.setEliminated(p.isEliminated());
            return dto;
        }).collect(Collectors.toList());

        result.setPlayers(players);

        long remaining = session.getPlayers().stream()
                .filter(p -> !p.isEliminated())
                .count();
        result.setRemainingPlayers((int) remaining);

        return result;
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

    public List<ArcadeRankingDto> getArcadeRanking(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<GameSession> topSessions = sessionRepository.findTopArcadeSessions(pageable);

        return topSessions.stream().map(session -> {
            ArcadeRankingDto dto = new ArcadeRankingDto();
            dto.setUserId(session.getHost().getId());
            dto.setUsername(session.getHost().getUsername());
            dto.setScore(session.getScore());
            dto.setEndedAt(session.getEndedAt());
            return dto;
        }).toList();
    }

    public List<ClassicRankingDto> getClassicLeaderboard() {
        return sessionRepository.findAll().stream()
                .filter(session -> !session.isMultiplayer() && !session.isArcadeMode() && session.isEnded())
                .sorted(Comparator.comparing(GameSession::getScore).reversed()
                        .thenComparing(GameSession::getEndedAt).reversed())
                .map(session -> {
                    ClassicRankingDto dto = new ClassicRankingDto();
                    dto.setUserId(session.getHost().getId());
                    dto.setUsername(session.getHost().getUsername());
                    dto.setScore(session.getScore());
                    dto.setTotalQuestions(session.getQuestionLimit());
                    dto.setEndedAt(session.getEndedAt().toString());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
