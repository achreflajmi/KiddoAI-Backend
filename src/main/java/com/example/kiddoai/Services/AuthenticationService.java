package com.example.kiddoai.Services;

import com.example.kiddoai.Entities.*;
import com.example.kiddoai.Repositories.ClasseRepository;
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
    @Autowired
    private final ClasseRepository classeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Autowired
    private ChatbotService chatbotService;

    public AuthenticationService(
            UserRepository userRepository, ClasseRepository classeRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.classeRepository = classeRepository;
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
        user.setParentPhoneNumber(input.getParentPhoneNumber()); // <— add this line
        user.setThreadId(chatbotService.createThread());
        user.setClasse(input.getClasse());
        // Validate role or default to KID
        String role = input.getRole();
        if (role == null || (!role.equals("ADMIN") && !role.equals("KID"))) {
            role = "KID";
        }
        user.setRole(Role.valueOf(role));



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
        //response.put("VectorStoreId", classeRepository.findByName(user.getClasse()).getClass());

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
        response.put("threadId", user.getThreadId()); // Include threadId
        response.put("vectorStoreId", getVectorStoreIdForClasse(user.getClasse()));


        return response;
    }

     /* -----------------------------------------------------------
       PRIVATE HELPERS
       ----------------------------------------------------------- */
    /**
     * Retrieves the Classe document by its name and returns the
     * associated vectorStoreId.
     *
     * @throws IllegalStateException if the class does not exist
     *                               or does not have a vectorStoreId.
     */
    private String getVectorStoreIdForClasse(String classeName) {
        return classeRepository.findByName(classeName)
                .map(Classe::getVectorStoreId)
                .filter(id -> id != null && !id.isBlank())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No vector store configured for classe '" + classeName + "'.")
                );
    }

}
