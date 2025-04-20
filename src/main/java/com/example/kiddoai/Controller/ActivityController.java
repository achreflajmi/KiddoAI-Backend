package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.Activity;
import com.example.kiddoai.Entities.Lesson;
import com.example.kiddoai.Entities.Subject;
import com.example.kiddoai.Repositories.ActivityRepository;
import com.example.kiddoai.Repositories.LessonRepository;
import com.example.kiddoai.Repositories.SubjectRepository;
import com.example.kiddoai.Services.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")  // Allow all origins
@RequiredArgsConstructor
@RestController
@RequestMapping("/Activity")
public class ActivityController {
    private final AiService aiService;
    private final LessonRepository lessonRepository;
    private final ActivityRepository activityRepository;

    public static class AccuracyRequest {
        private Double accuracy;

        // Getter
        public Double getAccuracy() {
            return accuracy;
        }

        // Setter
        public void setAccuracy(Double accuracy) {
            this.accuracy = accuracy;
        }
    }

    @GetMapping("/problems")
    public String getLatestActivityProblems() {
        // Fetch the Activity with the highest ID
        Optional<Activity> latestActivityOptional = activityRepository.findTopByOrderByIdDesc();

        // Check if an Activity was found and if it has problems
        // Using Optional's map and orElse for concise handling
        return latestActivityOptional
                .map(Activity::getProblems) // Extract the 'problems' field if activity exists
                .filter(problems -> problems != null && !problems.trim().isEmpty()) // Ensure problems are not null or just whitespace
                .orElse("Problems not found");
    }

    public static class ProblemRequestDto {
        private String lesson;
        private String subject;
        private String level;

        // Getters (needed for deserialization)
        public String getLesson() { return lesson; }
        public String getSubject() { return subject; }
        public String getLevel() { return level; }

        @Override
        public String toString() {
            return "ProblemRequestDto{" +
                    "lesson='" + lesson + '\'' +
                    ", subject='" + subject + '\'' +
                    ", level='" + level + '\'' +
                    '}';
        }
    }



    @PostMapping("/saveProblem") // Or keep /saveProblem if you prefer
    public String createActivityWithProblems(@RequestBody ProblemRequestDto requestDto) {
        // 1. Generate problems using the AI service
        String generatedProblems = aiService.generateProblems(
                requestDto.getLesson(),
                requestDto.getSubject(),
                requestDto.getLevel()
        );

        Activity newActivity = new Activity();
        // Lesson lesson = ;
        newActivity.setLessonid(lessonRepository.findLessonByName(requestDto.getLesson()).getId());
        newActivity.setProblems(generatedProblems); // Store the generated problems

        // 4. Save the new Activity to the database
        Activity savedActivity = activityRepository.save(newActivity);

        // 5. Return a response
        return ""+savedActivity;
    }

    @PostMapping("/updateActivityLesson")
    public ResponseEntity<String> updateActivityLesson(@RequestBody AccuracyRequest request) { // Accept Double directly
        // 1. Validate input
        Double accuracy = request.getAccuracy();
        if (accuracy == null) {
            return ResponseEntity.badRequest().body("Accuracy value is required in the request body.");
        }

        // 2. Find the latest activity
        Optional<Activity> latestActivityOpt = activityRepository.findTopByOrderByIdDesc();
        if (latestActivityOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No activities found to update.");
        }

        Activity latestActivity = latestActivityOpt.get();
        Long lessonId = Long.valueOf(latestActivity.getLessonid()); // Get lesson identifier for average calculation

        // 3. Update accuracy and save immediately
        latestActivity.setAccuracy(accuracy.floatValue()); // Assuming Activity uses Float, cast if needed
        activityRepository.save(latestActivity);

        // 4. Check lessonId before proceeding with average calculation and level adjustment
        if (lessonId == null ) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body("Accuracy updated for latest activity (ID: " + latestActivity.getId() + "), but lesson identifier is missing. Level not adjusted.");
        }

        // 5. Calculate average and adjust level
        float averageAccuracy = aiService.getAverageAccuracyOfLast5Activities(String.valueOf(lessonId));


        Lesson currentLesson = lessonRepository.findLessonsById(lessonId);
        Integer currentLevel = currentLesson.getLevel(); // Assuming getlevel() returns the current level

        String levelAdjustmentMessage = "Level remains unchanged.";
        boolean levelAdjusted = false;

        if (currentLevel != null) {
            if (averageAccuracy < 40.0f && currentLevel > 0) {
                currentLesson.setLevel(currentLevel - 1);
                levelAdjustmentMessage = String.format("Level decreased to %d based on average accuracy (< 40%%).", currentLesson.getLevel());
                levelAdjusted = true;
            } else if (averageAccuracy > 75.0f && currentLevel < 10) {
                currentLesson.setLevel(currentLevel + 1);
                levelAdjustmentMessage = String.format("Level increased to %d based on average accuracy (> 75%%).", currentLesson.getLevel());
                levelAdjusted = true;
            }
        } else {
            levelAdjustmentMessage = "Level not adjusted (current level is null).";
        }

        // 6. Save again *only* if level changed
        if (levelAdjusted) {
            activityRepository.save(latestActivity);
        }

        // 7. Return concise response
        String responseMsg = String.format("Accuracy updated for activity %d (Lesson: '%s'). Avg: %.2f. %s",
                latestActivity.getId(), lessonId, averageAccuracy, levelAdjustmentMessage);
        return ResponseEntity.ok(responseMsg);
    }

    @PostMapping("/add")
    public Activity addActivity(@RequestBody Activity activity) {
        // Optionally, check if the activity with the given ID exists and update it, or create a new one
        return activityRepository.save(activity);
    }
}