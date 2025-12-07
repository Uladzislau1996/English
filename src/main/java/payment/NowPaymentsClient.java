package payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class NowPaymentsClient {

    @Value("${nowpayments.api-key}")
    private String apiKey;

    @Value("${nowpayments.callback-url}")
    private String callbackUrl;

    @Value("${nowpayments.success-url}")
    private String successUrl;

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String createInvoice(Long telegramUserId) throws Exception {

        String bodyJson = objectMapper.createObjectNode()
                .put("price_amount", 1)
                .put("price_currency", "usdterc20")
                .put("pay_currency", "fiat")
                .put("order_id", String.valueOf(telegramUserId))
                .put("success_url", successUrl)
                .put("ipn_callback_url", callbackUrl)
                .toString();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.nowpayments.io/v1/invoice"))
                .header("x-api-key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() >= 300) {
            throw new IllegalStateException("Invoice request failed: " + resp.body());
        }

        return extractInvoiceUrl(resp.body());
    }

    private String extractInvoiceUrl(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        JsonNode urlNode = root.path("invoice_url");

        if (urlNode.isMissingNode() || urlNode.isNull()) {
            throw new IllegalStateException("Invoice URL was not returned: " + json);
        }

        return urlNode.asText();
    }
}
