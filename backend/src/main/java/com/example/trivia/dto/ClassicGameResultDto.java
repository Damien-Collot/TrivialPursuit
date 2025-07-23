package com.example.trivia.dto;

import lombok.Data;

@Data
public class ClassicGameResultDto {
    private int score;
    private int totalQuestions;
    private boolean success;
}
