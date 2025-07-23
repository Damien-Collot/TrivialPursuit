package com.example.trivia.dto;

import lombok.Data;

@Data
public class ClassicRankingDto {
    private Long userId;
    private String username;
    private int score;
    private int totalQuestions;
    private String endedAt;
}
