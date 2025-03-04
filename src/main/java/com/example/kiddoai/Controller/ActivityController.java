package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.Activity;
import com.example.kiddoai.Entities.Lesson;
import com.example.kiddoai.Entities.Subject;
import com.example.kiddoai.Repositories.ActivityRepository;
import com.example.kiddoai.Repositories.LessonRepository;
import com.example.kiddoai.Repositories.SubjectRepository;
import com.example.kiddoai.Services.AiService;
import lombok.RequiredArgsConstructor;
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


    @GetMapping("/Create/{prompt}")
    public String Activity(
            @PathVariable String prompt
    ){
        activityRepository.updateActivityCodeById(1L, aiService.generateHtml(prompt));

        return "activity code created !!";// return
    }

    @GetMapping("/problems")
    public String getPorblems() {
        Activity activity = activityRepository.findProblemsById(1L);
        if (activity != null && activity.getProblems() != null) {
            return activity.getProblems();
        }
        return "Problems not found";
    }


    @PostMapping("/problem")
    public String getProblem(@RequestBody String prompt) {
        activityRepository.updateActivityProblemsById(1L, aiService.generateProblems(prompt));
        return "activity problems created !!";
    }

    @PostMapping("/add")
    public Activity addActivity(@RequestBody Activity activity) {
        // Optionally, check if the activity with the given ID exists and update it, or create a new one
        return activityRepository.save(activity);
    }
}