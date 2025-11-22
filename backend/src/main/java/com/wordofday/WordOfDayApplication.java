package com.wordofday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WordOfDayApplication {
    public static void main(String[] args) {
        SpringApplication.run(WordOfDayApplication.class, args);
    }
}