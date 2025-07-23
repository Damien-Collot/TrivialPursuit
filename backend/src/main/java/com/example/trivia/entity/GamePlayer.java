package com.example.trivia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
    @Column
    private boolean eliminated;
    @Column
    private int score;
    @Column
    private boolean isHost;
    @Column
    private boolean ready;
    @Column(name = "answered_questions")
    private int answeredQuestions;
}
