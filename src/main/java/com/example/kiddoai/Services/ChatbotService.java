package com.example.kiddoai.Services;

import com.example.kiddoai.Config.UnicodeDecoder;
import com.example.kiddoai.Entities.User;
import com.example.kiddoai.Repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ChatbotService {

    @Value("${PYTHON_FLASK_URL}")
    private String pythonFlaskUrl;

    private WebClient webClient;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void initWebClient() {
        if (pythonFlaskUrl == null || pythonFlaskUrl.isBlank()) {
            throw new IllegalStateException("PYTHON_FLASK_URL is not set or is blank!");
        }
        this.webClient = WebClient.builder()
                .baseUrl(pythonFlaskUrl)
                .build();
    }

    // Create a new thread for a user
    public String createThread() {
        Mono<String> threadIdResponse = webClient.post()
                .uri("/create_thread")
                .retrieve()
                .bodyToMono(String.class);

        String threadIdJson = threadIdResponse.block();
        JSONObject jsonResponse = new JSONObject(threadIdJson);
        return jsonResponse.getString("thread_id");
    }

    // Send a message to the chatbot and get a response
    public String chatWithAssistant(String threadId, String userInput) {
        Mono<String> assistantResponse = webClient.post()
                .uri("/chat")
                .bodyValue(Map.of("thread_id", threadId, "text", userInput))
                .retrieve()
                .bodyToMono(String.class);

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

    // Transcribe audio to text and send to assistant
    public String transcribeAndChat(byte[] audioData, String threadId) {
        String transcriptionText = transcribeAudio(audioData);
        if (transcriptionText != null && !transcriptionText.isEmpty()) {
            return chatWithAssistant(threadId, transcriptionText);
        }
        return "Failed to transcribe audio.";
    }

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
        return extractTextFromTranscription(response);
    }

    private String extractTextFromTranscription(String transcriptionResponse) {
        JSONObject jsonResponse = new JSONObject(transcriptionResponse);
        String transcription = jsonResponse.getString("transcription");
        transcription = transcription.replaceAll("\\{\\n  \"text\" : \"", "")
                .replaceAll("\"\\n}", "")
                .replaceAll("\\\\n", " ")
                .replaceAll("\\\\", "");
        return transcription.trim();
    }

    public String teachLesson(String threadId, String lessonName, String subjectName) {
        String userInput = "كيدو، ممكن تشرح لي درس " + lessonName + " من مادة " + subjectName +
                " بطريقة مرحة وسهلة لفهمها. and no need to write the special characters such as emojies and (*) . " +
                "also no need to say بالطبع. just start explaining directly , cause for the user it will not be a " +
                "comunication it will be just a paraghrafph that a test to speech model will read to explain the lesson . " +
                "also i need you to make the explaination of the lesson involve the data you've extracted about the persona of the kid that way he will understand better";

        String response = chatWithAssistant(threadId, userInput);
        JSONObject jsonResponse = new JSONObject(response);
        String assistantResponse = jsonResponse.getString("response");

        return UnicodeDecoder.decodeUnicode(assistantResponse);
    }
}
