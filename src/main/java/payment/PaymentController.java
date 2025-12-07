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

        if (payload.payment_status.equalsIgnoreCase("finished")
                && payload.price_currency.equalsIgnoreCase("usdterc20")
                && payload.price_amount == 1.0) {

            long userId = Long.parseLong(payload.order_id);
            premiumService.activatePremium(userId);
        }

        return ResponseEntity.ok("OK");
    }
}