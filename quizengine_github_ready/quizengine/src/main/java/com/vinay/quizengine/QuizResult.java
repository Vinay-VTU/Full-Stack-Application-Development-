package com.vinay.quizengine;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "quiz_results")
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int score;
    private LocalDate date;

    public QuizResult() {}

    public QuizResult(String name, int score, LocalDate date) {
        this.name = name;
        this.score = score;
        this.date = date;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}