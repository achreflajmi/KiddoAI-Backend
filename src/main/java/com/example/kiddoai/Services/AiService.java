package com.example.kiddoai.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
public class AiService {
    private final WebClient webClient;

    public AiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://cd81-197-31-134-30.ngrok-free.app").build(); // Use localhost for testing
    }

    public String generateHtml(String prompt) {
        Mono<String> htmlResponse = webClient.post()
                .uri("/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("prompt", prompt))
                .retrieve()
                .bodyToMono(String.class);

        return htmlResponse.block(); // Blocking to get the response synchronously

    }
    public String generateProblems(String prompt) {
        Mono<String> jsonResponse = webClient.post()
                .uri("/problems")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("prompt", prompt))
                .retrieve()
                .bodyToMono(String.class);

        return jsonResponse.block(); // Blocking to get the response synchronously

    }

}