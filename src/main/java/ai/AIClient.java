package ai;

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

    public String askAI(String userMessage) throws Exception {

        String prompt = """
                You are an English tutor bot.
                Tasks:
                1) Answer the user's message in natural English.
                2) Provide a corrected version of the user's sentence.
                3) Explain briefly what was wrong.

                User message: %s
                """.formatted(userMessage);

        String bodyJson = """
                {
                  "model": "deepseek-chat",
                  "messages": [
                    {"role": "user", "content": "%s"}
                  ]
                }
                """.formatted(prompt.replace("\"", "\\\""));

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.deepseek.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        return extractContent(resp.body());
    }

    private String extractContent(String json) {
        // Очень простое извлечение (лучше сделать через Jackson)
        int i = json.indexOf("\"content\"");
        int start = json.indexOf("\"", i + 10) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}