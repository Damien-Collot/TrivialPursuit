package com.example.trivia.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PlayerStatusDto {
    private Long userId;
    private String username;
    private int score;
    private boolean eliminated;
}
