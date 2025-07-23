package com.example.trivia.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Data
@Getter
@Setter
public class GameSessionSummaryDto {
    private Long sessionId;
    private boolean multiplayer;
    private boolean arcadeMode;
    private boolean ended;
    private int score;
    private Instant startedAt;
    private Instant endedAt;
    private List<Integer> categoryFilter;
    public long getDurationInSeconds() {
        if (endedAt != null && startedAt != null) {
            return endedAt.getEpochSecond() - startedAt.getEpochSecond();
        }
        return 0;
    }
    public String getStatus() {
        return ended ? "Completed" : "In Progress";
    }

}
