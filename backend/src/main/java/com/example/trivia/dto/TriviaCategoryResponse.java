package com.example.trivia.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class TriviaCategoryResponse {
    private List<TriviaCategory> trivia_categories;
}

