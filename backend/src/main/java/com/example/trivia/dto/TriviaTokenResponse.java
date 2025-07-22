package com.example.trivia.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class TriviaTokenResponse {
    private int response_code;
    private String token;

}
