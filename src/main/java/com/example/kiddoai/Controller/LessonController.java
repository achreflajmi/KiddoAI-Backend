package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.Lesson;
import com.example.kiddoai.Repositories.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")  // Allow all origins
@RequiredArgsConstructor
@RestController
@RequestMapping("/Lesson")
public class LessonController {
    private final LessonRepository lessonRepository;

    @GetMapping("/bySubject/{subject}")
    public List<Lesson> getLessons(
            @PathVariable String subject
    ) {
        return lessonRepository.findAllBySubject(subject);
    }
}
