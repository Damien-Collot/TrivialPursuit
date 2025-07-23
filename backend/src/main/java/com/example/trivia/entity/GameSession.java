package com.example.trivia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
public class GameSession {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User host;
    @Column(name = "classic_mode")
    private boolean classicMode;

    @Column(name = "question_limit")
    private int questionLimit;

    @Column(name = "questions_answered")
    private int questionsAnswered;

    @Column(name = "difficulty")
    private String difficulty;
    @Column
    private boolean multiplayer;
    @Column
    private boolean arcadeMode;
    @Column
    private boolean started;
    @Column
    private boolean ended;
    @Column
    private int score;
    @Column
    private Instant startedAt;
    @Column
    private Instant endedAt;
    @Column
    private String token;
    @Column
    private String currentCorrectAnswer;
    @Column(name = "current_question_number")
    private int currentQuestionNumber;
    @ElementCollection
    private List<Integer> categoryFilter;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL)
    private List<GamePlayer> players;
}

