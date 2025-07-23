package com.example.trivia.repository;

import com.example.trivia.entity.GameSession;
import com.example.trivia.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByUserIdOrderByPlayedAtDesc(Long userId);
    List<GameSession> findByHostOrderByStartedAtDesc(User user);
    @Query("SELECT s FROM GameSession s WHERE s.arcadeMode = true AND s.ended = true ORDER BY s.score DESC, s.endedAt ASC")
    List<GameSession> findTopArcadeSessions(Pageable pageable);
}
