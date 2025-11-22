package com.wordofday.repository;

import com.wordofday.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {
    Optional<Word> findByWordDate(LocalDate date);

    boolean existsByWordDate(LocalDate date);

    void deleteByWordDate(LocalDate date);
}