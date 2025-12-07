package ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Simplified AI client that provides offline corrections without paid APIs
@Service
public class AIClient {

    @Value("${deepseek.api-key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();

    public String askAI(String userMessage) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode root = mapper.createObjectNode();
            root.put("model", "deepseek-chat");

            ArrayNode messages = mapper.createArrayNode();

            ObjectNode system = mapper.createObjectNode();
            system.put("role", "system");
            system.put("content",
                    "You are a friendly English conversation partner named Alex. " +
                            "You must ALWAYS correct the user's English before replying. " +
                            "You must clearly explain their mistakes and then continue the conversation naturally. " +
                            "Be friendly, supportive and talk like a real human being."
            );

            ObjectNode user = mapper.createObjectNode();
            user.put("role", "user");
            user.put("content", userMessage);

            messages.add(system);
            messages.add(user);

            root.set("messages", messages);

            String body = mapper.writeValueAsString(root);

            Request request = new Request.Builder()
                    .url("https://api.deepseek.com/v1/chat/completions")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .post(RequestBody.create(body, MediaType.parse("application/json")))
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                return "AI request failed: " + response.body().string();
            }

            String json = response.body().string();
            return extractMessage(json);

        } catch (Exception e) {
            return "Sorry, I couldn't reach the AI right now: " + e.getMessage();
        }
    }

    private String extractMessage(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            return root
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();
        } catch (JsonProcessingException e) {
            // Jackson не смог распарсить JSON
            return "AI returned an unexpected response (JSON error): " + e.getMessage();
        } catch (Exception e) {
            // На всякий случай — любые другие ошибки
            return "AI returned an unexpected response: " + e.getMessage();
        }
    }
}