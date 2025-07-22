package com.example.trivia.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role = "USER";

    @OneToMany(mappedBy = "user")
    private List<GameSession> sessions;

    // Getters and setters
}
