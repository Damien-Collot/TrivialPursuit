package com.example.trivia.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ArcadeRankingDto {
    private Long userId;
    private String username;
    private int score;
    private Instant endedAt;
}
