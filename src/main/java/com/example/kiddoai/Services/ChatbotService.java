package com.example.kiddoai.Services;

import com.example.kiddoai.Config.UnicodeDecoder;
import com.example.kiddoai.Entities.User;
import com.example.kiddoai.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.json.JSONObject;

import java.util.Map;

@Service
public class ChatbotService {

    private final WebClient webClient;

    @Autowired
    private UserRepository userRepository;

    public ChatbotService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://454d-197-2-189-134.ngrok-free.app").build(); // Replace with your actual Python backend URL
    }

    // Create a new thread for a user
    public String createThread() {
        Mono<String> threadIdResponse = webClient.post()
                .uri("/create_thread")
                .retrieve()
                .bodyToMono(String.class);

        // Extract the thread_id from the JSON response
        String threadIdJson = threadIdResponse.block();
        JSONObject jsonResponse = new JSONObject(threadIdJson);
        return jsonResponse.getString("thread_id");  // Extract thread_id as a simple string
    }

    // Send a message to the chatbot and get a response
    public String chatWithAssistant(String threadId, String userInput) {
        Mono<String> assistantResponse = webClient.post()
                .uri("/chat")
                .bodyValue(Map.of("thread_id", threadId, "text", userInput)) // Sending threadId and user input to the Python backend
                .retrieve()
                .bodyToMono(String.class);

        // Block to get the actual response string
        String response = assistantResponse.block();

        return UnicodeDecoder.decodeUnicode(response);
    }


    // Send a welcoming message that includes IQ score without user consent
    public String welcoming(String threadId, String IQCategory) {
        User user = userRepository.findByThreadId(threadId);
        if (user != null) {
            user.setIQCategory(IQCategory);
            userRepository.save(user);
        }
        return "Welcome message sent with IQ category: " + IQCategory;
    }

    // In your ChatbotService class, ensure you handle the transcription correctly:
    public String transcribeAndChat(byte[] audioData, String threadId) {
        // Step 1: Transcribe the audio to text
        String transcriptionText = transcribeAudio(audioData);

        // Step 2: Send the transcribed text to the chatbot as a message
        if (transcriptionText != null && !transcriptionText.isEmpty()) {
            return chatWithAssistant(threadId, transcriptionText); // Send transcribed text to chatbot
        }

        return "Failed to transcribe audio.";
    }

    // Transcribe audio into text
    private String transcribeAudio(byte[] audioData) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("audio", new ByteArrayResource(audioData))
                .header("Content-Disposition", "form-data; name=\"audio\"; filename=\"audio.wav\"");

        Mono<String> transcriptionResponse = webClient.post()
                .uri("/transcribe")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .retrieve()
                .bodyToMono(String.class);

        String response = transcriptionResponse.block();
        return extractTextFromTranscription(response);  // Return cleaned transcription
    }

    // Extract and clean the transcription text from the response
    private String extractTextFromTranscription(String transcriptionResponse) {
        // Ensure the transcription response is in a clean format
        JSONObject jsonResponse = new JSONObject(transcriptionResponse);
        String transcription = jsonResponse.getString("transcription");

        // Clean up the transcription text
        transcription = transcription.replaceAll("\\{\\n  \"text\" : \"", "");
        transcription = transcription.replaceAll("\"\\n}", "");
        transcription = transcription.replaceAll("\\\\n", " ");  // Convert '\n' to spaces
        transcription = transcription.replaceAll("\\\\", "");     // Remove any escape characters

        return transcription.trim(); // Final cleaned transcription text
    }
    public String teachLesson(String threadId, String lessonName, String subjectName) {
        // Construct the dynamic message
        String userInput = "كيدو، ممكن تشرح لي درس " + lessonName + " من مادة " + subjectName + " بطريقة مرحة وسهلة لفهمها" + ". and no need to write the special characters such as emojies and (*) . also no need to say بالطبع" + ". just start explaining directly , cause for the user it will not be a comunication it will be just a paraghrafph that a test to speech model will read to explain the lesson . also i need you to make the explaination of the lesson involve the data you've extracted about the persona of the kid that way he will undtrestand better";

        // Send the message and get the response
        String response = chatWithAssistant(threadId, userInput);

        // Extract the response from the JSON (just the plain string part without JSON structure)
        JSONObject jsonResponse = new JSONObject(response);
        String assistantResponse = jsonResponse.getString("response");

        // Decode the response from Unicode to Arabic text
        return UnicodeDecoder.decodeUnicode(assistantResponse);
    }


}
