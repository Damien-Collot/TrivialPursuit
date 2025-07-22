package com.example.trivia.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class QuestionDto {
    private String category;
    private String type;
    private String difficulty;
    private String question;
    private String correctAnswer;
    private List<String> incorrectAnswers;
}

