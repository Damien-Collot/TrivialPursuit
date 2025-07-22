package com.example.trivia.controller;


import com.example.trivia.service.GameSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final GameSessionService sessionService;

    public UserController(GameSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<?> getUserSessions(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.findByUserId(id));
    }
}
