package com.example.trivia.controller;

import com.example.trivia.dto.TriviaCategory;
import com.example.trivia.dto.TriviaCategoryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class TriviaCategoryController {

    private final RestTemplate restTemplate;
    private final String triviaApiBaseUrl;

    public TriviaCategoryController(@Value("${opentdb.base-url}") String triviaApiBaseUrl) {
        this.triviaApiBaseUrl = triviaApiBaseUrl;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            String url = triviaApiBaseUrl + "/api_category.php";
            TriviaCategoryResponse response = restTemplate.getForObject(url, TriviaCategoryResponse.class);
            if (response != null) {
                return ResponseEntity.ok(response.getTrivia_categories());
            } else  {
                return ResponseEntity.badRequest().body("No categories found");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e);
        }
    }
}
