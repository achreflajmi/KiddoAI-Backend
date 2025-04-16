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

    // A simple inner class for accuracy requests
    public static class AccuracyRequest {
        private Double accuracy;

        public Double getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(Double accuracy) {
            this.accuracy = accuracy;
        }
    }

    @GetMapping("/Create/{prompt}")
    public String Activity(
            @PathVariable String prompt,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ){
        // You can log the authHeader if desired:
        System.out.println("Auth header (optional): " + authHeader);
        // activityRepository.updateActivityCodeById(1L, aiService.generateHtml(prompt));
        return "activity code created !!";
    }

    @GetMapping("/problems")
    public String getLatestActivityProblems(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Optional<Activity> latestActivityOptional = activityRepository.findTopByOrderByIdDesc();
        return latestActivityOptional
                .map(Activity::getProblems)
                .filter(problems -> problems != null && !problems.trim().isEmpty())
                .orElse("Problems not found");
    }

    public static class ProblemRequestDto {
        private String lesson;
        private String subject;
        private String level;

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

    @PostMapping("/saveProblem")
    public String createActivityWithProblems(
            @RequestBody ProblemRequestDto requestDto,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        // Optionally log the token
        System.out.println("Auth header (optional): " + authHeader);

        String generatedProblems = aiService.generateProblems(
                requestDto.getLesson(),
                requestDto.getSubject(),
                requestDto.getLevel()
        );

        Activity newActivity = new Activity();
        newActivity.setLessonid(lessonRepository.findLessonByName(requestDto.getLesson()).getId());
        newActivity.setProblems(generatedProblems);

        Activity savedActivity = activityRepository.save(newActivity);
        return "" + savedActivity;
    }

    @PostMapping("/updateActivityLesson")
    public ResponseEntity<String> updateActivityLesson(
            @RequestBody AccuracyRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Double accuracy = request.getAccuracy();
        if (accuracy == null) {
            return ResponseEntity.badRequest().body("Accuracy value is required in the request body.");
        }

        Optional<Activity> latestActivityOpt = activityRepository.findTopByOrderByIdDesc();
        if (latestActivityOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No activities found to update.");
        }

        Activity latestActivity = latestActivityOpt.get();
        Long lessonId = Long.valueOf(latestActivity.getLessonid());

        latestActivity.setAccuracy(accuracy.floatValue());
        activityRepository.save(latestActivity);

        if (lessonId == null ) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body("Accuracy updated for latest activity (ID: " + latestActivity.getId() + "), but lesson identifier is missing. Level not adjusted.");
        }

        float averageAccuracy = aiService.getAverageAccuracyOfLast5Activities(String.valueOf(lessonId));
        Lesson currentLesson = lessonRepository.findLessonsById(lessonId);
        Integer currentLevel = currentLesson.getLevel();

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

        if (levelAdjusted) {
            activityRepository.save(latestActivity);
        }

        String responseMsg = String.format("Accuracy updated for activity %d (Lesson: '%s'). Avg: %.2f. %s",
                latestActivity.getId(), lessonId, averageAccuracy, levelAdjustmentMessage);
        return ResponseEntity.ok(responseMsg);
    }

    @PostMapping("/add")
    public Activity addActivity(
            @RequestBody Activity activity,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        System.out.println("Auth header (optional): " + authHeader);
        return activityRepository.save(activity);
    }
}
