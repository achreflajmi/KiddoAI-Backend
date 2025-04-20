package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.Classe;
import com.example.kiddoai.Entities.Lesson;
import com.example.kiddoai.Entities.Subject;
import com.example.kiddoai.Repositories.ClasseRepository;
import com.example.kiddoai.Repositories.LessonRepository;
import com.example.kiddoai.Repositories.SubjectRepository;
import com.example.kiddoai.Services.FlaskAssistantService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:63176", "http://localhost:56995", "http://localhost:58771"})

@RestController
@RequestMapping("/adminDashboard")
public class AdminController {
    @Autowired
    private FlaskAssistantService flaskAssistantService;


    @Autowired
    private ClasseRepository classeRepo;

    @Autowired
    private SubjectRepository subjectRepo;

    @Autowired
    private LessonRepository lessonRepo;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/classes")
    public Classe addClasse(@RequestBody Classe classeRequest) {
        try {
            // 1. Create vector store using Flask service
            String vectorStoreId = flaskAssistantService.createVectorStore(classeRequest.getName());

            // 2. Create and save Classe with vectorStoreId
            Classe newClasse = new Classe();
            newClasse.setName(classeRequest.getName());
            newClasse.setSubjectIds(new ArrayList<>());
            newClasse.setVectorStoreId(vectorStoreId);

            return classeRepo.save(newClasse);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create class and link vector store: " + e.getMessage());
        }
    }



    // Get all grades
    @GetMapping("/classes")
    public List<Classe> getAllClasses() {
        return classeRepo.findAll();
    }



    // Add subject to a grade
    @PreAuthorize("hasRole('ADMIN')")

    @PostMapping("/classes/{classeName}/subjects")
    public Subject addSubject(@PathVariable String classeName, @RequestBody Subject subject) {
        subject.setClasseName(classeName);
        subject.setLessonIds(new ArrayList<>());
        return subjectRepo.save(subject);
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





    // Add lesson to a subject
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/subjects/{subjectName}/lessons")
    public ResponseEntity<Lesson> addLessonToSubject(@PathVariable String subjectName, @RequestBody Lesson lessonRequest) {
        // Manually generate a unique ID if it's null
        if (lessonRequest.getId() == null) {
            lessonRequest.setId(System.currentTimeMillis());  // or use a Sequence Generator if needed
        }

        lessonRequest.setSubject(subjectName);
        lessonRequest.setActivitieIds(new ArrayList<>());

        Lesson savedLesson = lessonRepo.save(lessonRequest);
        return ResponseEntity.ok(savedLesson);
    }




    // View lessons of a subject
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/subjects/by-name/{subjectName}/lessons")
    public List<Lesson> getLessonsBySubjectName(@PathVariable String subjectName) {
        return lessonRepo.findAllBySubject(subjectName.trim());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/uploadLessonPDF")
    public ResponseEntity<String> uploadLessonPDF(
            @RequestParam("pdf") MultipartFile file, // ✅ Must be "pdf"
            @RequestParam("vector_store_id") String vectorStoreId // ✅ Must be "vector_store_id"
    ) {
        try {
            byte[] fileBytes = file.getBytes();
            String response = flaskAssistantService.uploadPDF(fileBytes, file.getOriginalFilename(), vectorStoreId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }



    @PreAuthorize("hasRole('KID')")
    @PostMapping(
            value = "/configureVectorStore",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> configureVectorStore(@RequestBody Map<String, String> body) {

        /* ---------- logger ---------- */
        Logger log = LoggerFactory.getLogger(AdminController.class);

        log.info("▶︎ /configureVectorStore  body = {}", body);

        String vectorStoreId = body.get("vector_store_id");
        if (vectorStoreId == null || vectorStoreId.isBlank()) {
            log.warn("✖︎ vector_store_id missing or blank");
            return ResponseEntity.badRequest()
                    .body("{\"error\":\"vector_store_id is required\"}");
        }

        try {
            log.info("⤵︎ Calling Flask /configure_vector_store  id={}", vectorStoreId);
            String flaskResp = flaskAssistantService.configureVectorStore(vectorStoreId.trim());
            log.info("⤴︎ Flask response → {}", flaskResp);

            // Return Flask’s JSON exactly as‑is
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(flaskResp);

        } catch (Exception ex) {
            log.error("⚠️  Flask call failed", ex);
            String msg = ex.getMessage() != null ? ex.getMessage() : "unknown error";
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"" + msg.replace("\"", "\\\"") + "\"}");
        }
    }

}
