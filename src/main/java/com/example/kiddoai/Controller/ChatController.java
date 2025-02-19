package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.ChatRequest;
import com.example.kiddoai.Services.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/chat")
@Tag(name = "Chatbot", description = "Chat with the AI Assistant")
public class ChatController {

    @Autowired
    private ChatbotService chatbotService;

    // Endpoint to chat with the assistant
    @Operation(
            summary = "Chat with the AI Assistant",
            description = "Send a message to the AI Assistant and get a response. Provide threadId and user input."
    )
    @PostMapping("/send")
    public String chat(@RequestBody ChatRequest chatRequest) {
        // Send the message and get the assistant's response
        return chatbotService.chatWithAssistant(chatRequest.getThreadId(), chatRequest.getUserInput());
    }
    // Endpoint to send a welcoming message automatically with the IQ score
    @Operation(
            summary = "Send a welcoming message with the IQ score",
            description = "Automatically sends a welcoming message with the user's IQ score to the assistant. The assistant will respond without user input."
    )
    @PostMapping("/welcome")
    public String welcoming(@RequestBody WelcomeRequest welcomeRequest) {
        return chatbotService.welcoming(welcomeRequest.getThreadId(), welcomeRequest.getIQCategory());
    }


    // Endpoint to transcribe audio
    @PostMapping("/transcribe")
    public String transcribeAudio(@RequestParam("audio") MultipartFile audioFile) {
        try {
            // Convert MultipartFile to byte[] (if needed by Flask backend)
            byte[] audioData = audioFile.getBytes();
            return chatbotService.transcribeAudio(audioData); // Send to service
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}