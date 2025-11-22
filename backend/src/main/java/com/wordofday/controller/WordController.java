package com.wordofday.controller;

import com.wordofday.model.Word;
import com.wordofday.service.AIService;
import com.wordofday.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/words")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class WordController {
    private final WordService wordService;
    private final AIService aiService;

    @GetMapping("/today")
    public ResponseEntity<Word> getTodayWord() {
        return wordService.getTodayWord()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/history")
    public ResponseEntity<List<Word>> getWordHistory() {
        return ResponseEntity.ok(wordService.getAllWords());
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateWord() {
        try {
            aiService.generateAndSaveWord(LocalDate.now());
            return ResponseEntity.ok("Word generated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to generate word: " + e.getMessage());
        }
    }
}