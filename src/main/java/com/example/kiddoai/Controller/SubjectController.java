package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.Subject;
import com.example.kiddoai.Repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectRepository subjectRepository;

    // Endpoint to add a new subject
    @PostMapping("/add")
    public Subject addSubject(@RequestBody Subject subject) {
        return subjectRepository.save(subject);
    }

    // Endpoint to get all subjects
    @GetMapping("/all")
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }
}
