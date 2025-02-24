package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.Activity;
import com.example.kiddoai.Entities.Lesson;
import com.example.kiddoai.Entities.Subject;
import com.example.kiddoai.Repositories.ActivityRepository;
import com.example.kiddoai.Repositories.LessonRepository;
import com.example.kiddoai.Repositories.SubjectRepository;
import com.example.kiddoai.Services.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

    @GetMapping("/html")
    public String getHtmlFile() {
        Activity activity = activityRepository.findCodeById(1L);
        if (activity != null && activity.getCode() != null) {
            return activity.getCode();
        }
        return "Code not found";
    }
}