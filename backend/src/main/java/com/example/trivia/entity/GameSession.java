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

    private boolean multiplayer;
    private boolean arcadeMode;
    private boolean started;
    private boolean ended;
    private int score;
    private Instant startedAt;
    private Instant endedAt;

    private String token;

    @ElementCollection
    private List<Integer> categoryFilter;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL)
    private List<GamePlayer> players;
}

