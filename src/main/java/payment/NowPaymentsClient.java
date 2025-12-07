package payment;

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

    public String createInvoice(Long telegramUserId) throws Exception {

        String bodyJson = """
                {
                  "price_amount": 1,
                  "price_currency": "usdterc20",
                  "pay_currency": "fiat",
                  "order_id": "%s",
                  "success_url": "%s",
                  "ipn_callback_url": "%s"
                }
                """.formatted(telegramUserId, successUrl, callbackUrl);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.nowpayments.io/v1/invoice"))
                .header("x-api-key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        // Возвращаем ссылку на оплату
        return extractInvoiceUrl(resp.body());
    }

    private String extractInvoiceUrl(String json) {
        // например, json: {"id":123,"invoice_url":"https://nowpayments..."}
        int i = json.indexOf("invoice_url");
        int start = json.indexOf("\"", i + 13) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}