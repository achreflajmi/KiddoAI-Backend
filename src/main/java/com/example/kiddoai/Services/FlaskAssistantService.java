package com.example.kiddoai.Services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class FlaskAssistantService {

    private final WebClient webClient;

    @Autowired
    public FlaskAssistantService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://flask_ai_model:5009") // or your ngrok URL
                .build();
    }

    public String createThread() {
        Mono<String> response = webClient.post()
                .uri("/create_thread")
                .retrieve()
                .bodyToMono(String.class);

        String json = response.block();
        JSONObject obj = new JSONObject(json);
        return obj.getString("thread_id");
    }

    public String createVectorStore(String name) {
        Mono<String> response = webClient.post()
                .uri("/create_vector_store")
                .bodyValue(Map.of("name", name))
                .retrieve()
                .bodyToMono(String.class);

        String json = response.block();
        JSONObject obj = new JSONObject(json);
        return obj.getString("vector_store_id");
    }

    public String configureVectorStore(String vectorStoreId) {
        Mono<String> response = webClient.post()
                .uri("/configure_vector_store")
                .bodyValue(Map.of("vector_store_id", vectorStoreId))
                .retrieve()
                .bodyToMono(String.class);

        return response.block(); // JSON string response
    }

    public String teach(String threadId, String text) {
        Mono<String> response = webClient.post()
                .uri("/teach")
                .bodyValue(Map.of(
                        "thread_id", threadId,
                        "text", text
                ))
                .retrieve()
                .bodyToMono(String.class);

        String json = response.block();
        JSONObject obj = new JSONObject(json);
        return obj.getString("response");
    }

    public String uploadPDF(byte[] fileData, String fileName, String vectorStoreId) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("pdf", new ByteArrayResource(fileData))
                .header("Content-Disposition", "form-data; name=\"pdf\"; filename=\"" + fileName + "\"");
        builder.part("vector_store_id", vectorStoreId);

        Mono<String> response = webClient.post()
                .uri("/upload_pdf")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .retrieve()
                .bodyToMono(String.class);

        return response.block();
    }
}