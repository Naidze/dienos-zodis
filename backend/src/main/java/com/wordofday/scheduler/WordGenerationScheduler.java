package com.wordofday.scheduler;

import com.wordofday.service.WordService;
import com.wordofday.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class WordGenerationScheduler {
    private final WordService wordService;
    private final AIService aiService;

    @Scheduled(cron = "${word.generation.cron}")
    public void generateDailyWord() {
        log.info("Starting daily word generation...");

        LocalDate today = LocalDate.now();

        if (wordService.wordExistsForDate(today)) {
            log.info("Word already exists for today: {}", today);
            return;
        }

        try {
            // Generate word using AI
            aiService.generateAndSaveWord(today);
            log.info("Successfully generated word for: {}", today);
        } catch (Exception e) {
            log.error("Failed to generate word for {}: {}", today, e.getMessage());
        }
    }
}