package payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import premium.PremiumService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PremiumService premiumService;

    @PostMapping("/callback")
    public ResponseEntity<?> callback(@RequestBody PaymentCallback payload) {

        if (payload != null
                && "finished".equalsIgnoreCase(payload.payment_status)
                && "usdterc20".equalsIgnoreCase(payload.price_currency)
                && payload.price_amount == 1.0) {

            long userId = Long.parseLong(payload.order_id);
            premiumService.activatePremium(userId);
        }

        return ResponseEntity.ok("OK");
    }
}
