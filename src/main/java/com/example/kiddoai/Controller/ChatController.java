package com.example.kiddoai.Controller;

import com.example.kiddoai.Services.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public String chat(
            @Parameter(description = "Thread ID of the user to identify the conversation", required = true)
            @RequestParam String threadId,
            @Parameter(description = "User input message to send to the chatbot", required = true)
            @RequestParam String userInput) {
        // Send the message and get the assistant's response
        return chatbotService.chatWithAssistant(threadId, userInput);
    }

    // Endpoint to send a welcoming message automatically with the IQ score
    @Operation(
            summary = "Send a welcoming message with the IQ score",
            description = "Automatically sends a welcoming message with the user's IQ score to the assistant. The assistant will respond without user input."
    )
    @PostMapping("/welcome")
    public String welcoming(
            @Parameter(description = "Thread ID of the user to identify the conversation", required = true)
            @RequestParam String threadId,
            @Parameter(description = "User's IQ score", required = true)
            @RequestParam float niveauIQ) {
        // Send the welcoming message and get the assistant's response
        return chatbotService.welcoming(threadId, niveauIQ);
    }
}
