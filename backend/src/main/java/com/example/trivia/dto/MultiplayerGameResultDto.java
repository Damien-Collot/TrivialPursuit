package com.example.trivia.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class MultiplayerGameResultDto {
    private boolean correct;
    private boolean gameEnded;
    private int playerScore;
    private List<PlayerStatusDto> players;
    private int remainingPlayers;
}
