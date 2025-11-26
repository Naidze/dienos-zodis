package com.wordofday.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class HealthController {

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        log.info("Health check requested");
        return ResponseEntity.ok("UP");
    }
}
