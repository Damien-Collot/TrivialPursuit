package com.example.trivia.controller;

import com.example.trivia.dto.*;
import com.example.trivia.entity.GamePlayer;
import com.example.trivia.entity.GameSession;
import com.example.trivia.repository.GamePlayerRepository;
import com.example.trivia.repository.GameSessionRepository;
import com.example.trivia.service.GameSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class GameSessionController {

    private final GameSessionService gameSessionService;
    private final GamePlayerRepository gamePlayerRepository;

    public GameSessionController(GameSessionService gameSessionService,  GamePlayerRepository gamePlayerRepository) {
        this.gameSessionService = gameSessionService;
        this.gamePlayerRepository = gamePlayerRepository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GameSessionSummaryDto>> getUserSessions(@PathVariable Long userId) {
        return ResponseEntity.ok(gameSessionService.getSessionsByUser(userId));
    }

    @GetMapping("/ranking/arcade")
    public ResponseEntity<List<ArcadeRankingDto>> getArcadeRanking(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(gameSessionService.getArcadeRanking(limit));
    }

    @GetMapping("/ranking/classic")
    public ResponseEntity<List<ClassicRankingDto>> getClassicLeaderboard() {
        return ResponseEntity.ok(gameSessionService.getClassicLeaderboard());
    }

    @PostMapping("/solo/arcade")
    public ResponseEntity<GameSession> startSoloArcade(@RequestParam Long userId,
                                                       @RequestBody(required = false) List<Integer> categories,
                                                       @RequestParam(required = false) String difficulty) {
        return ResponseEntity.ok(gameSessionService.startSoloArcadeSession(userId, categories, difficulty));
    }

    @PostMapping("/solo/classic")
    public ResponseEntity<GameSession> startClassicSession(@RequestParam Long userId,
                                                           @RequestParam Integer questionLimit,
                                                           @RequestParam(required = false) String difficulty,
                                                           @RequestBody(required = false) List<Integer> categories) {
        return ResponseEntity.ok(gameSessionService.startClassicSession(userId, categories, difficulty, questionLimit));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<GameSession> getSessionStatus(@PathVariable Long sessionId) {
        return ResponseEntity.ok(gameSessionService.getSession(sessionId));
    }

    @GetMapping("/{sessionId}/question")
    public ResponseEntity<QuestionDto> getNextQuestion(@PathVariable Long sessionId) {
        return ResponseEntity.ok(gameSessionService.fetchNextQuestion(sessionId));
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<SoloGameAnswerResultDto> submitAnswer(
            @PathVariable Long sessionId,
            @RequestParam String userAnswer) {
        return ResponseEntity.ok(gameSessionService.submitAnswer(sessionId, userAnswer));
    }

    @PostMapping("/multiplayer/create")
    public ResponseEntity<GameSession> createMultiplayer(@RequestParam Long hostId,
                                                         @RequestParam boolean arcadeMode,
                                                         @RequestParam boolean classicMode,
                                                         @RequestParam(required = false) Integer questionLimit,
                                                         @RequestParam(required = false) String difficulty,
                                                         @RequestBody(required = false) List<Integer> categories) {
        return ResponseEntity.ok(gameSessionService.createMultiplayerSession(
                hostId, arcadeMode, classicMode, questionLimit, difficulty, categories
        ));
    }

    @PostMapping("/multiplayer/join")
    public ResponseEntity<GamePlayer> joinMultiplayer(@RequestParam Long userId,
                                                      @RequestParam Long sessionId) {
        return ResponseEntity.ok(gameSessionService.joinMultiplayerSession(userId, sessionId));
    }

    @PostMapping("/multiplayer/{sessionId}/start")
    public ResponseEntity<Void> startMultiplayerGame(@PathVariable Long sessionId) {
        gameSessionService.startMultiplayerGame(sessionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/multiplayer/{sessionId}/ready")
    public ResponseEntity<Void> setPlayerReady(@PathVariable Long sessionId,
                                               @RequestParam Long userId,
                                               @RequestParam boolean ready) {
        gameSessionService.setPlayerReady(sessionId, userId, ready);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/multiplayer/{sessionId}/answer")
    public ResponseEntity<MultiplayerGameResultDto> submitAnswerMultiplayer(@PathVariable Long sessionId,
                                                                            @RequestParam Long userId,
                                                                            @RequestParam String userAnswer) {
        MultiplayerGameResultDto result = gameSessionService.submitAnswerMultiplayer(sessionId, userId, userAnswer);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{sessionId}/result")
    public ResponseEntity<MultiplayerGameFinalResultDto> getMultiplayerGameResult(@PathVariable Long sessionId) {
        GameSession session = gameSessionService.getSession(sessionId);
        if (!session.isEnded()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<GamePlayer> players = gamePlayerRepository.findByGameSessionId(sessionId);

        MultiplayerGameFinalResultDto result = new MultiplayerGameFinalResultDto();
        result.setSessionId(session.getId());
        result.setMode(session.isArcadeMode() ? "arcade" : "classic");
        result.setStartedAt(session.getStartedAt());
        result.setEndedAt(session.getEndedAt());
        result.setPlayerResults(
                players.stream().map(p -> new MultiplayerGameFinalResultDto.PlayerResult(
                        p.getUser().getUsername(), p.getScore(), p.isEliminated())
                ).toList()
        );

        return ResponseEntity.ok(result);
    }

}

