package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.LoginUserDto;
import com.example.kiddoai.Entities.RegisterUserDto;
import com.example.kiddoai.Services.AuthenticationService;
import com.example.kiddoai.Services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Authentication", description = "Endpoints for user authentication")
@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;


    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns an access token and refresh token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully",
                            content = @Content(schema = @Schema(implementation = Map.class)))
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterUserDto registerUserDto) {
        Map<String, Object> response = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "User login",
            description = "Authenticates a user and returns an access token and refresh token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                            content = @Content(schema = @Schema(implementation = Map.class)))
            }
    )
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody LoginUserDto loginUserDto) {
        Map<String, Object> response = authenticationService.authenticate(loginUserDto);
        return ResponseEntity.ok(response);
    }
}
