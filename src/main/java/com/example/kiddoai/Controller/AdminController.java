package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.Classe;
import com.example.kiddoai.Entities.Lesson;
import com.example.kiddoai.Entities.Subject;
import com.example.kiddoai.Repositories.ClasseRepository;
import com.example.kiddoai.Repositories.LessonRepository;
import com.example.kiddoai.Repositories.SubjectRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@CrossOrigin(origins = {"http://localhost:63176", "http://localhost:56995", "http://localhost:59603"})

@RestController
@RequestMapping("/adminDashboard")
public class AdminController {
    @Autowired
    private ClasseRepository classeRepo;

    @Autowired
    private SubjectRepository subjectRepo;

    @Autowired
    private LessonRepository lessonRepo;

    // Get all grades
    @GetMapping("/classes")
    public List<Classe> getAllClasses() {
        return classeRepo.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")

    @GetMapping("/classes/{classeName}/subjects")
    public List<Subject> getSubjectsByClasse(@PathVariable String classeName) {
        // Trim the incoming parameter
        String trimmedClass = classeName.trim();
        List<Subject> subjects = subjectRepo.getSubjectsByClasseName(trimmedClass);
        System.out.println("📥 Incoming request for subjects in class: '" + trimmedClass + "'. Found: " + subjects.size());

        // Log each subject's name and classeName field
        for (Subject s : subjects) {
            System.out.println("Subject: " + s.getName() + ", ClasseName: '" + s.getClasseName() + "'");
        }

        return subjects;
    }




    // Add subject to a grade
    @PreAuthorize("hasRole('ADMIN')")

    @PostMapping("/classes/{classeName}/subjects")
    public Subject addSubject(@PathVariable String classeName, @RequestBody Subject subject) {
        subject.setClasseName(classeName);
        subject.setLessonIds(new ArrayList<>());
        return subjectRepo.save(subject);
    }

    // Add lesson to a subject
    @PreAuthorize("hasRole('ADMIN')")

    @PostMapping("/subjects/{subjectId}/lessons")
    public Lesson addLesson(@PathVariable String subjectId, @RequestBody Lesson lesson) {
        lesson.setSubject(subjectId);
        lesson.setActivitieIds(new ArrayList<>());
        Lesson savedLesson = lessonRepo.save(lesson);

        // Update subject
        Subject subject = subjectRepo.findById(String.valueOf(new ObjectId(subjectId))).get();
        subject.getLessonIds().add(savedLesson.getId().toString());
        subjectRepo.save(subject);

        return savedLesson;
    }

    // View lessons of a subject
    @PreAuthorize("hasRole('ADMIN')")

    @GetMapping("/subjects/{subjectId}/lessons")
    public List<Lesson> getLessonsBySubject(@PathVariable String subjectId) {
        return lessonRepo.findAllBySubject(subjectId);
    }
}

