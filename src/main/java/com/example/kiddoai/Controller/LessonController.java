package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.Lesson;
import com.example.kiddoai.Entities.Subject;
import com.example.kiddoai.Repositories.LessonRepository;
import com.example.kiddoai.Repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Lesson")
@RequiredArgsConstructor
public class LessonController {

    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;

    // Endpoint to add a lesson and associate it with a subject
    @PostMapping("/add")
    public Lesson addLesson(@RequestBody Lesson lesson) {
        // Fetch subject to associate with the lesson
        Subject subject = subjectRepository.findByName(lesson.getSubject());

        if (subject != null) {
            // Add the lesson to the subject's list of lessonIds
            subject.getLessonIds().add(String.valueOf(lesson.getId()));
            subjectRepository.save(subject);
        }

        return lessonRepository.save(lesson);  // Save lesson to the database
    }

    // Endpoint to get lessons by subject
    @GetMapping("/bySubject/{subject}")
    public List<Lesson> getLessonsBySubject(@PathVariable String subject) {
        return lessonRepository.findAllBySubject(subject);
    }
}
