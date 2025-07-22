package com.example.trivia.entity;

import jakarta.persistence.*;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int score;
    private boolean isHost;
    private boolean ready;
}
