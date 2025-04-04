package com.example.kiddoai.Controller;

import com.example.kiddoai.Entities.ChatRequest;
import com.example.kiddoai.Entities.TeachLessonRequest;
import com.example.kiddoai.Services.AssistantCService;
import com.example.kiddoai.Services.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/chat")
@Tag(name = "Chatbot", description = "Chat with the AI Assistant")
public class ChatController {

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private AssistantCService assistantCService;
    // Endpoint to chat with the assistant
    @Operation(
            summary = "Chat with the AI Assistant",
            description = "Send a message to the AI Assistant and get a response. Provide threadId and user input."
    )
    @PostMapping("/send")
    public String chat(@RequestBody ChatRequest chatRequest) {
        // 1) Send the child's message to your Kiddo backend
        String rawResponse = chatbotService.chatWithAssistant(chatRequest.getThreadId(), chatRequest.getUserInput());

        // 2) Also analyze the child's message via AssistantCService
        assistantCService.analyzeChildMessage(chatRequest.getThreadId(), chatRequest.getUserInput());

        // 3) Return the normal Kiddo response
        return rawResponse;
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

    // Modified transcribe endpoint to handle the transcription and chat logic
    @PostMapping("/transcribe")
    public String transcribeAndChat(@RequestParam("audio") MultipartFile audioFile,
                                    @RequestParam(value = "threadId", required = false) String threadId) {
        try {
            if (threadId == null || threadId.isEmpty()) {
                return "Error: 'threadId' parameter is missing or empty.";
            }

            byte[] audioData = audioFile.getBytes();
            String transcriptionText = chatbotService.transcribeAndChat(audioData, threadId);

            // Extract just the transcription text from the response
            JSONObject jsonResponse = new JSONObject(transcriptionText);
            String responseText = jsonResponse.getString("response");  // Get the value of "response"

            return responseText;  // Return only the actual transcription text
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }




    // Save the audio file to the server (no change needed here)
    private String saveAudioFile(MultipartFile audioFile, String fileName) throws IOException {
        // Define the directory where audio files will be saved
        File directory = new File("uploads/audio_files"); // Save in the 'uploads/audio_files' directory

        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

        // Define the file path
        File audioFilePath = new File(directory, fileName);

        // Save the file
        try (FileOutputStream fos = new FileOutputStream(audioFilePath)) {
            fos.write(audioFile.getBytes());
        }

        // Return the path where the file is saved
        return audioFilePath.getAbsolutePath();
    }
    @Operation(
            summary = "Teach a lesson to the assistant",
            description = "Automatically sends a lesson teaching request to the assistant. The assistant will respond with an explanation."
    )
    @PostMapping("/teach_lesson")
    public String teachLesson(@RequestBody TeachLessonRequest teachLessonRequest) {
        // Call the teachLesson method with the provided parameters
        return chatbotService.teachLesson(teachLessonRequest.getThreadId(), teachLessonRequest.getLessonName(), teachLessonRequest.getSubjectName());
    }
}
