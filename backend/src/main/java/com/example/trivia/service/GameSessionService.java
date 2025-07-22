package com.example.trivia.service;

import com.example.trivia.entity.GameSession;
import com.example.trivia.repository.GameSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameSessionService {

    private final GameSessionRepository repository;

    public GameSessionService(GameSessionRepository repository) {
        this.repository = repository;
    }

    public GameSession save(GameSession session) {
        session.setPlayedAt(LocalDateTime.now());
        return repository.save(session);
    }

    public List<GameSession> findByUserId(Long userId) {
        return repository.findByUserIdOrderByPlayedAtDesc(userId);
    }
}
