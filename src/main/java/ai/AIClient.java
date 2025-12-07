package ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Сервис, отвечающий за запросы к нейросети DeepSeek
@Service
public class AIClient {

    @Value("${deepseek.api-key}")
    private String apiKey;

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String askAI(String userMessage) throws Exception {

        String prompt = """
                You are an English tutor bot.
                Tasks:
                1) Reply to the user only in English and keep a friendly tone.
                2) Provide a corrected version of the user's sentence.
                3) Briefly explain the corrections.

                User message: %s
                """.formatted(userMessage);

        String bodyJson = objectMapper.createObjectNode()
                .put("model", "deepseek-chat")
                .putArray("messages")
                .add(objectMapper.createObjectNode()
                        .put("role", "user")
                        .put("content", prompt))
                .toString();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.deepseek.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() >= 300) {
            throw new IllegalStateException("AI request failed: " + resp.body());
        }

        return extractContent(resp.body());
    }

    private String extractContent(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.path("choices").path(0).path("message").path("content");

        if (contentNode.isMissingNode() || contentNode.isNull()) {
            throw new IllegalStateException("No content returned by AI");
        }

        return contentNode.asText();
    }
}
