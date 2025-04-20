package com.example.kiddoai.Services;

import com.example.kiddoai.Entities.User;
import com.example.kiddoai.Repositories.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
/**
 * AssistantCService:
 * Uses the standard OpenAI ChatCompletion endpoint with a system message
 * that holds detection/summary instructions. Returns JSON to see if there's a problem.
 */
@Service
public class AssistantCService {

    Dotenv dotenv = Dotenv.load();
    String OPENAI_API_KEY = dotenv.get("OPENAI_API_KEY");
    // Put your real OpenAI API key here

    // The system instructions you previously had in your custom assistant.
    // We place them directly in the 'system' message now.
    private static final String SYSTEM_PROMPT = """
            ROLE: You are a "Content Safety & Topic Detector" specialized in analyzing a single child message (in Arabic dialect, possibly partial French/English).
                                                                         
                                                                         GOAL:
                                                                         1) Detect if the child’s message contains or indicates a serious problem:
✅ Expanded Problem Types to Add:
🔴 Mental Health
self-harm (e.g., cutting, hurting oneself)

depression (sadness, crying, loss of interest)

anxiety or fear (panic, nightmares, stress)

feeling unwanted ("ما يحبني حد", "نفسيتي تعبة")

🧍‍♂️ Social & Emotional Problems
isolation or loneliness ("ما عنديش صحاب", "أنا وحدي دايمًا")

rejection or exclusion (being left out)

low self-esteem ("أنا غبي", "ما ننجحش في شيء")

🚫 Abuse & Exploitation
verbal abuse (swearing, insults, humiliation)

domestic violence (hearing/seeing violence at home)

sexual abuse or inappropriate talk

exploitation (being used, threatened for something)

⚠️ Risky Behavior
running away or talk about disappearing

online threats / grooming (e.g., weird messages from strangers)

stealing or pressured to steal

🧪 Substance Exposure
drugs (mentions of pills, weed, cannabis, etc.)

medication misuse ("خذيت دوا من غير سبب")

📚 Academic Struggles
school failure or pressure ("ما نفهمش الدروس", "أبوي يضربني كان ما ننجحش")

fear of teacher or punishment ("المعلم يصرخ عليّ")
   - Self-harm or suicide attempts (e.g., "نحب ننتحر", "جرحت روحي", "ما نحبش نعيش").

                                                                         2) If there is NO such problem, respond with this exact JSON (no extra text):
                                                                            {"flagged": false}
                                                                         3) If there IS a problem, respond with JSON including:
                                                                            {
                                                                              "flagged": true,
                                                                              "problemType": "<string describing the main category, e.g. bullying, self-harm, smoking>",
                                                                              "summary": "<short Arabic explanation for the parent>"
                                                                            }
                                                                            - "problemType" must be consistent among repeated messages about the same topic.
                                                                            - "summary" must be a short, plain text in Arabic (Tunisian or standard) that a parent can read.
                                                                         4) Output must be valid JSON only — no commentary or additional keys.
                                                                         5) Write "problemType" in plain English or Arabic (e.g., "bullying", "dangerous_behavior", "self_harm").
                                                                         
                                                                         Example:
                                                                         - If child says: "ضربوني في المدرسة"، respond:
                                                                           {"flagged": true, "problemType": "bullying", "summary": "تعرض ولدك للضرب في المدرسة ويحس بالخوف"}
                                                                         
                                                                         - If child says: "أنا فرحان نلعب مع صحابي"، respond:
                                                                           {"flagged": false}
                                                                           - If child says: "نحب ننتحر وما عادش نتحمل"، respond:
                                                                                         {"flagged": true, "problemType": "suicide_attempt", "summary": "ولدك عبّر عن رغبة في الانتحار، الرجاء التدخل الفوري والحديث معه بلطف"}
                                                                         
""";

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private UserRepository userRepository;

    private final WebClient openAiClient;

    public AssistantCService() {
        // Build a WebClient to talk to the standard openai.com/v1 endpoints
        this.openAiClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + OPENAI_API_KEY)
                .build();
    }

    /**
     * Called each time the child sends a new message.
     * - If the LLM flags a new problem type => send SMS to parent.
     * - If not flagged or same problem type => skip.
     */
    public void analyzeChildMessage(String threadId, String childMessage) {
        // 1) Find the user
        User user = userRepository.findByThreadId(threadId);
        if (user == null) {
            System.out.println("[AssistantCService] No user found with threadId=" + threadId);
            return;
        }

        // 2) Call LLM with system instructions + user message
        JSONObject detectionResult = callOpenAiAssistant(childMessage);
        if (detectionResult == null) {
            System.out.println("[AssistantCService] LLM returned null or invalid JSON; skipping.");
            return;
        }

        // Expect shape: {"flagged": false} or {"flagged": true, "problemType": "...", "summary": "..."}
        boolean flagged = detectionResult.optBoolean("flagged", false);
        if (!flagged) {
            System.out.println("[AssistantCService] Child message is not flagged => no alert.");
            return;
        }

        // If flagged == true
        String newProblemType = detectionResult.optString("problemType", "unknown");
        String summary = detectionResult.optString("summary", "No summary provided.");

        String lastType = user.getLastProblemType();
        LocalDateTime lastTimestamp = user.getLastProblemTimestamp();
        LocalDateTime now = LocalDateTime.now();

        boolean shouldSendAlert = false;

        if (!newProblemType.equalsIgnoreCase(lastType)) {
            shouldSendAlert = true;
        } else if (lastTimestamp == null || ChronoUnit.HOURS.between(lastTimestamp, now) >= 24) {
            shouldSendAlert = true;
        }

        if (shouldSendAlert) {
            System.out.println("[AssistantCService] Sending alert: " + newProblemType);
            String parentPhone = user.getParentPhoneNumber();
            if (parentPhone != null && !parentPhone.isBlank()) {
                twilioService.sendSms(parentPhone, summary);
            }
            user.setLastProblemType(newProblemType);
            user.setLastProblemTimestamp(now);
            userRepository.save(user);
        } else {
            System.out.println("[AssistantCService] Alert skipped. Same type and sent < 24h ago.");
        }
    }


    /**
     * Calls the standard /v1/chat/completions endpoint with a system + user message.
     * We rely on GPT to produce JSON like {"flagged":..., "problemType":..., "summary":...}.
     */
    private JSONObject callOpenAiAssistant(String childMessage) {
        try {
            // 1) Build messages array with system prompt + user message
            JSONArray messages = new JSONArray();

            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", SYSTEM_PROMPT);
            messages.put(systemMsg);

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", childMessage);
            messages.put(userMsg);

            // 2) Construct request body with model, messages, etc.
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");  // or "gpt-4" if available
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.0);

            // 3) Make the request
            String responseStr = openAiClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (responseStr == null) {
                System.out.println("[AssistantCService] Null response from OpenAI");
                return null;
            }

            // 4) Parse the top-level response
            JSONObject jsonResp = new JSONObject(responseStr);
            JSONArray choices = jsonResp.optJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                System.out.println("[AssistantCService] No choices returned. Full resp = " + responseStr);
                return null;
            }

            // 5) Get the content from the first choice
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject messageObj = firstChoice.getJSONObject("message");
            String content = messageObj.getString("content");

            // 6) content should be a JSON string, e.g. {"flagged":true,"problemType":"bullying","summary":"..."}
            return new JSONObject(content);

        } catch (Exception e) {
            System.out.println("[AssistantCService] Error calling OpenAI: " + e.getMessage());
            return null;
        }
    }
}
