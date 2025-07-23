package com.example.trivia.dto;

import lombok.Data;

@Data
public class SoloGameAnswerResultDto {
    private boolean correct;
    private boolean ended;
    private String mode;
    private int score;
    private int questionsAnswered;
    private int questionLimit;
}
