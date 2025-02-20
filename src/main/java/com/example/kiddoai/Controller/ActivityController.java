package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.Subject;
import com.example.kiddoai.Repositories.SubjectRepository;
import com.example.kiddoai.Services.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
@RestController
@RequestMapping("/Activity")
public class ActivityController {
    private final AiService aiService;
    private final SubjectRepository subjectRepository;

    @PostMapping("/Create")
    public String Activity(
            @RequestBody String prompt
    ){

        return aiService.generateHtml(prompt);
    }

    @GetMapping("/test")
    public String getHtmlFile() {
        Subject subject = new Subject();
        subject.setId(1L);
        subject.setName("Maths");
        subjectRepository.save(subject);
        // Return the HTML content fetched from Flask
        return subjectRepository.findAll().toString();
    }
}