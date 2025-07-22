package com.example.trivia.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class GameSession {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private int score;
    private int questionCount;
    private LocalDateTime playedAt;


}
