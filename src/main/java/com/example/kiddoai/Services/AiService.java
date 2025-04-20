package com.example.kiddoai.Services;

import com.example.kiddoai.Entities.Activity;
import com.example.kiddoai.Repositories.ActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AiService {
    private final WebClient webClient;
    private final ActivityRepository activityRepository;


    public AiService(WebClient.Builder webClientBuilder, ActivityRepository activityRepository) {
        this.webClient = webClientBuilder.baseUrl("https://4305-102-152-215-66.ngrok-free.app").build(); // Use localhost for testing
        this.activityRepository = activityRepository;
    }

    public String generateProblems(String lesson, String subject, String level) {
        // Create the request body Map with the new fields
        Map<String, String> requestBody = Map.of(
                "lesson", lesson,
                "subject", subject,
                "level", level
        );

        Mono<String> jsonResponse = webClient.post()
                .uri("/problems")
                .contentType(MediaType.APPLICATION_JSON)
                // Use the new map as the request body
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);

        // Blocking to get the response synchronously
        // Be cautious using .block() in a reactive application, consider returning Mono<String> instead if possible
        return jsonResponse.block();
    }

    public float getAverageAccuracyOfLast5Activities(String lessonId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Activity> activities = activityRepository.findTop5ByLessonIdOrderByCreatedAtDesc(lessonId, sort);

        if (activities.isEmpty()) {
            throw new IllegalArgumentException("No activities found for lessonId: " + lessonId);
        }

        // Optional: Require at least 5 activities
        // if (activities.size() < 5) {
        //     return -1.0f; // Or throw an exception
        // }

        List<Activity> lastFive = activities.size() > 5 ? activities.subList(0, 5) : activities;

        float totalAccuracy = 0.0f;
        for (Activity activity : lastFive) {
            totalAccuracy += activity.getAccuracy();
        }

        System.out.println("Calculated average from " + lastFive.size() + " activities");
        return totalAccuracy / lastFive.size();
    }

}