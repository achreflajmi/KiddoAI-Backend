package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.Lesson;
import com.example.kiddoai.Entities.Subject;
import com.example.kiddoai.Repositories.LessonRepository;
import com.example.kiddoai.Repositories.SubjectRepository;
import com.example.kiddoai.Services.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "*")  // Allow all origins
@RequiredArgsConstructor
@RestController
@RequestMapping("/Activity")
public class ActivityController {
    private final AiService aiService;
    private final LessonRepository lessonRepository;

    @GetMapping("/Create/{prompt}")
    public String Activity(
            @PathVariable String prompt
    ){

        return aiService.generateHtml(prompt);
    }

    @GetMapping("/test")
    public String getHtmlFile() {
        Lesson lesson = new Lesson();
        lesson.setId(1L);
        lesson.setSubject("maths");
        lesson.setName("الجمع والطرح");
        lesson.setDescription("addition and substraction");
        lessonRepository.save(lesson);
        // Return the HTML content fetched from Flask
        return lessonRepository.findAll().toString();
    }
}