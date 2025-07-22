package com.example.trivia.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Data
@Getter
@Setter
public class TriviaQuestionResponse {
    private int response_code;
    private List<QuestionDto> results;

}
