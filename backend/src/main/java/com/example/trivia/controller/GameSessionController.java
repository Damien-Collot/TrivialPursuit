package com.example.trivia.controller;

import com.example.trivia.entity.GameSession;
import com.example.trivia.service.GameSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
public class GameSessionController {

    private final GameSessionService sessionService;

    public GameSessionController(GameSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody GameSession session) {
        return ResponseEntity.ok(sessionService.save(session));
    }
}
