package com.example.trivia.controller;

import com.example.trivia.dto.GameSessionSummaryDto;
import com.example.trivia.dto.QuestionDto;
import com.example.trivia.entity.GameSession;
import com.example.trivia.service.GameSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class GameSessionController {

    private final GameSessionService gameSessionService;

    public GameSessionController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GameSessionSummaryDto>> getUserSessions(@PathVariable Long userId) {
        return ResponseEntity.ok(gameSessionService.getSessionsByUser(userId));
    }

    @PostMapping("/solo/start")
    public ResponseEntity<GameSession> startSoloArcade(@RequestParam Long userId,
                                                       @RequestBody(required = false) List<Integer> categories) {
        return ResponseEntity.ok(gameSessionService.startSoloArcadeSession(userId, categories));
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
    public ResponseEntity<?> submitAnswer(@PathVariable Long sessionId,
                                          @RequestParam String userAnswer) {
        boolean correct = gameSessionService.submitAnswer(sessionId, userAnswer);
        return ResponseEntity.ok(Map.of("correct", correct));
    }
}

