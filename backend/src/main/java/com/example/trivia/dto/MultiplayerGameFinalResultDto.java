package com.example.trivia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class MultiplayerGameFinalResultDto {
    private Long sessionId;
    private String mode;
    private Instant startedAt;
    private Instant endedAt;
    private List<PlayerResult> playerResults;

    @Data
    @AllArgsConstructor
    public static class PlayerResult {
        private String username;
        private int score;
        private boolean eliminated;
    }
}

