package com.wordofday.service;

import com.wordofday.model.Word;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {
    private final WordService wordService;
    private final CloudinaryService cloudinaryService;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${groq.base.url}")
    private String groqBaseUrl;

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.model.name}")
    private String groqModelName;

    @Value("${cloudflare.account.id}")
    private String cloudflareAccountId;

    @Value("${cloudflare.api.token}")
    private String cloudflareApiToken;

    @Value("${cloudflare.ai.model}")
    private String cloudflareAiModel;

    @Value("${ai.prompt.word}")
    private String wordPromptTemplate;

    @Value("${ai.prompt.image}")
    private String imagePromptTemplate;

    public void generateAndSaveWord(LocalDate date) {
        try {
            log.info("Starting word generation for date: {}", date);

            // Step 1: Generate word text using Groq
            String wordData = generateWordWithGroq();
            log.info("Received word data: {}", wordData);

            JsonNode wordJson = objectMapper.readTree(wordData);

            String lithuanianWord = wordJson.get("word").asText();
            String definitionLt = wordJson.get("definition_lt").asText();
            String definitionEn = wordJson.get("definition_en").asText();
            String usageExampleLt = wordJson.get("usage_example_lt").asText();
            String usageExampleEn = wordJson.get("usage_example_en").asText();

            log.info("Generated word: {}", lithuanianWord);

            // Step 2: Generate image using Cloudflare AI and upload to Cloudinary
            String imageUrl = null;
            try {
                byte[] imageBytes = generateImageWithCloudflare(
                        lithuanianWord,
                        definitionEn,
                        usageExampleEn
                );

                String fileName = sanitizeFileName(lithuanianWord) + "_" + System.currentTimeMillis();
                imageUrl = cloudinaryService.uploadImage(imageBytes, fileName);

                log.info("Image uploaded to Cloudinary: {}", imageUrl);
            } catch (Exception e) {
                log.error("Failed to generate/upload image, continuing without it: {}", e.getMessage());
            }

            // Step 3: Save to database
            Word word = new Word();
            word.setWord(lithuanianWord);
            word.setDefinitionLt(definitionLt);
            word.setDefinitionEn(definitionEn);
            word.setUsageExampleLt(usageExampleLt);
            word.setUsageExampleEn(usageExampleEn);
            word.setImageUrl(imageUrl);
            word.setWordDate(date);

            wordService.saveWord(word);
            log.info("Successfully saved word: {}", lithuanianWord);

        } catch (Exception e) {
            log.error("Error generating word: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate word", e);
        }
    }

    private String generateWordWithGroq() {
        WebClient client = webClientBuilder
                .baseUrl(groqBaseUrl)
                .defaultHeader("Authorization", "Bearer " + groqApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

        String prompt = wordPromptTemplate;

        Map<String, Object> requestBody = Map.of(
                "model", groqModelName,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.8,
                "max_tokens", 500
        );

        try {
            String response = client.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(body -> {
                                        log.error("Groq API Error: {}", body);
                                        return new RuntimeException("API Error: " + body);
                                    })
                    )
                    .bodyToMono(String.class)
                    .block();

            log.info("Raw Groq Response: {}", response);

            JsonNode responseJson = objectMapper.readTree(response);
            String generatedText = responseJson
                    .get("choices").get(0)
                    .get("message").get("content").asText();

            log.info("Generated text: {}", generatedText);

            String cleanedJson = extractJson(generatedText);

            // Validate
            JsonNode validatedJson = objectMapper.readTree(cleanedJson);
            if (!validatedJson.has("word") ||
                    !validatedJson.has("definition_lt") ||
                    !validatedJson.has("definition_en") ||
                    !validatedJson.has("usage_example_lt") ||
                    !validatedJson.has("usage_example_en")) {
                throw new RuntimeException("Generated JSON missing required fields");
            }

            return cleanedJson;

        } catch (Exception e) {
            log.error("Error calling Groq API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate word with Groq: " + e.getMessage(), e);
        }
    }

    private byte[] generateImageWithCloudflare(String word, String definitionEn, String usageExampleEn) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build();

        WebClient client = webClientBuilder
                .baseUrl("https://api.cloudflare.com")
                .defaultHeader("Authorization", "Bearer " + cloudflareApiToken)
                .defaultHeader("Content-Type", "application/json")
                .exchangeStrategies(strategies)
                .build();

        String imagePrompt = imagePromptTemplate
                .replace("{word}", word)
                .replace("{definition}", definitionEn)
                .replace("{example}", usageExampleEn);

        log.info("Image generation prompt: {}", imagePrompt);

        Map<String, Object> requestBody = Map.of("prompt", imagePrompt);

        try {
            byte[] imageBytes = client.post()
                    .uri(String.format("/client/v4/accounts/%s/ai/run/%s",
                            cloudflareAccountId, cloudflareAiModel))
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("Cloudflare API Error: {}", body);
                                        return Mono.error(new RuntimeException("Cloudflare API Error: " + body));
                                    })
                    )
                    .bodyToMono(byte[].class)
                    .block();

            if (imageBytes == null || imageBytes.length == 0) {
                throw new RuntimeException("Received empty image data from Cloudflare");
            }

            log.info("Received image data: {} bytes", imageBytes.length);
            return imageBytes;

        } catch (Exception e) {
            log.error("Error generating image with Cloudflare: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate image: " + e.getMessage(), e);
        }
    }

    private String extractJson(String text) {
        text = text.replaceAll("```json\\s*", "").replaceAll("```\\s*", "");

        int jsonStart = text.indexOf("{");
        int jsonEnd = text.lastIndexOf("}") + 1;

        if (jsonStart >= 0 && jsonEnd > jsonStart) {
            return text.substring(jsonStart, jsonEnd).trim();
        }

        throw new RuntimeException("No valid JSON found in response: " + text);
    }

    private String sanitizeFileName(String word) {
        return word.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
    }
}