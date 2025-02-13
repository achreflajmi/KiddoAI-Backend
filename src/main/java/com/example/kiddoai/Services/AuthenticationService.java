package com.example.kiddoai.Services;

import com.example.kiddoai.Entities.LoginUserDto;
import com.example.kiddoai.Entities.RegisterUserDto;
import com.example.kiddoai.Entities.User;
import com.example.kiddoai.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService {
    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Autowired
    private ChatbotService chatbotService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    public Map<String, Object> signup(RegisterUserDto input) {
        // Create a new user
        User user = new User();
        user.setNom(input.getNom());
        user.setPrenom(input.getPrenom());
        user.setDateNaissance(input.getDateNaissance());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setScoreTotal(0.0f); // Default score
        user.setFavoriteCharacter(input.getFavoriteCharacter());
        user.setThreadId(chatbotService.createThread());

        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Build the response
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("threadId", user.getThreadId());

        return response;
    }
    public Map<String, Object> authenticate(LoginUserDto input) {
        // Authenticate the user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        // Find the user
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Build the response
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

        return response;
    }
}
