package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.Subject;
import com.example.kiddoai.Entities.User;
import com.example.kiddoai.Repositories.SubjectRepository;
import com.example.kiddoai.Repositories.UserRepository;
import com.example.kiddoai.Services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserServiceImpl userService;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public SubjectRepository subjectRepo;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/updateIQCategory")
    public ResponseEntity<?> updateIQCategory(@RequestBody Map<String, String> requestData) {
        String threadId = requestData.get("threadId");
        String iqCategory = requestData.get("IQCategory");

        User user = userRepository.findByThreadId(threadId);
        if (user != null) {
            user.setIQCategory(iqCategory);
            userRepository.save(user);
            return ResponseEntity.ok("IQ Category updated successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
    }

    // New endpoint for updating user profile
    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser) {
        // Find user by threadId
        User existingUser = userRepository.findByThreadId(updatedUser.getThreadId());
        if (existingUser != null) {
            // Update user details
            existingUser.setNom(updatedUser.getNom());
            existingUser.setPrenom(updatedUser.getPrenom());
            existingUser.setDateNaissance(updatedUser.getDateNaissance());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setIQCategory(updatedUser.getIQCategory());
            existingUser.setFavoriteCharacter(updatedUser.getFavoriteCharacter());

            // Save updated user
            userRepository.save(existingUser);
            return ResponseEntity.ok("Profile updated successfullyy!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
    }
    @GetMapping("/subjects")
    public List<Subject> getSubjectsForKid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return subjectRepo.getSubjectsByClasseName(user.getClasse());
    }
}