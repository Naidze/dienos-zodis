package com.wordofday.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "words")
@Data
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String word;

    // New separate language fields
    @Column(name = "definition_lt", columnDefinition = "TEXT")
    private String definitionLt;

    @Column(name = "definition_en", columnDefinition = "TEXT")
    private String definitionEn;

    @Column(name = "usage_example_lt", columnDefinition = "TEXT")
    private String usageExampleLt;

    @Column(name = "usage_example_en", columnDefinition = "TEXT")
    private String usageExampleEn;

    private String imageUrl;

    @Column(nullable = false, unique = true)
    private LocalDate wordDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}