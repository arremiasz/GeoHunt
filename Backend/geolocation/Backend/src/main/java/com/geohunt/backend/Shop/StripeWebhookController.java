package com.geohunt.backend.Shop;

import com.geohunt.backend.database.*;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvents(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            if ("checkout.session.completed".equals(event.getType())) {

                Session session = (Session) event.getData().getObject();

                long userId = Long.parseLong(session.getMetadata().get("userId"));
                long shopId = Long.parseLong(session.getMetadata().get("shopId"));

                Account user = accountRepository.findById(userId).orElse(null);
                Shop shop = shopRepository.findById(shopId).orElse(null);

                Transactions t = new Transactions();
                t.setUser(user);
                t.setShopItem(shop);
                t.setPrice(shop.getPrice());
                t.setDate(new Date());

                transactionsRepository.save(t);
            }

            return ResponseEntity.ok("Received");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Webhook Error: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "OK";
    }
}
