package com.example.trivia.repository;

import com.example.trivia.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByUserIdOrderByPlayedAtDesc(Long userId);
}
