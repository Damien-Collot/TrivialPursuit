package com.example.trivia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class User {
    @Id @GeneratedValue
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;
    @Column
    private String password;
    @Column
    private String role = "USER";

    @OneToMany(mappedBy = "user")
    private List<GameSession> sessions;

}
