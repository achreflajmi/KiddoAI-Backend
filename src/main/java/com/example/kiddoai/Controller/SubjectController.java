package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.Subject;
import com.example.kiddoai.Repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/Subject")
@RequiredArgsConstructor
public class SubjectController {

    // Use only one repository instance.
    private final SubjectRepository subjectRepository;

    // Endpoint to get all subjects (open to everyone)
    @GetMapping("/all")
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    // Admin-only endpoint to add a new subject for a given class.
    // This endpoint overrides any classeName provided in the request body with the one in the path.
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/classes/{classeName}/subjects")
    public Subject addSubject(@PathVariable String classeName, @RequestBody Subject subject) {
        // Override classeName to ensure consistency.
        subject.setClasseName(classeName);
        // Initialize lessonIds if not provided.
        if (subject.getLessonIds() == null) {
            subject.setLessonIds(new ArrayList<>());
        }
        return subjectRepository.save(subject);
    }


}
