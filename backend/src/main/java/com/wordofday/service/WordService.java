package com.wordofday.service;

import com.wordofday.model.Word;
import com.wordofday.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordService {
    private final WordRepository wordRepository;
    private final CloudinaryService cloudinaryService;

    public Optional<Word> getTodayWord() {
        return wordRepository.findByWordDate(LocalDate.now());
    }

    public List<Word> getAllWords() {
        return wordRepository.findAll();
    }

    public Word saveWord(Word word) {
        return wordRepository.save(word);
    }

    public boolean wordExistsForDate(LocalDate date) {
        return wordRepository.existsByWordDate(date);
    }

    public Optional<Word> getWordById(Long id) {
        return wordRepository.findById(id);
    }

    public void deleteWordForDate(LocalDate date) {
        Optional<Word> word = wordRepository.findByWordDate(date);
        if (word.isPresent()) {
            Word existingWord = word.get();
            // Delete image from Cloudinary if it exists
            if (existingWord.getImageUrl() != null && !existingWord.getImageUrl().isEmpty()) {
                String publicId = cloudinaryService.extractPublicId(existingWord.getImageUrl());
                if (publicId != null) {
                    cloudinaryService.deleteImage(publicId);
                }
            }
            // Delete word from database
            wordRepository.deleteByWordDate(date);
        }
    }
}