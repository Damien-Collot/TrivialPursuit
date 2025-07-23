package com.example.trivia.repository;

import com.example.trivia.entity.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    Optional<GamePlayer> findByGameSessionIdAndUserId(Long sessionId, Long userId);
    List<GamePlayer> findByGameSessionId(Long sessionId);
}
