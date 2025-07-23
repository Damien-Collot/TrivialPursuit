package com.example.trivia.dto;

import lombok.Data;

@Data
public class MultiplayerQuestionBroadcast {
    private Long sessionId;
    private String questionText;
    private String[] answers;
}
