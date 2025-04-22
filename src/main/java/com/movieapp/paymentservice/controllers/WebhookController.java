package com.movieapp.paymentservice.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieapp.paymentservice.services.MongoService;
import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class WebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final MongoService mongoService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // 💡 for manual JSON parsing

    public WebhookController(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                
                JsonNode root = objectMapper.readTree(payload);
                JsonNode metadataNode = root.path("data").path("object").path("metadata");

                String userEmail = metadataNode.path("email").asText(null);
                String tier = metadataNode.path("tier").asText(null);

                //ruchira
                //testing
                System.out.println("************************************************************************");
                System.out.println("🔔 Received Webhook!");
                System.out.println("Payload: " + payload);
                System.out.println("Event Type: " + event.getType());
                System.out.println("📩 Parsed metadata -> Email: " + userEmail + ", Tier: " + tier);
    
                System.out.println("************************************************************************");

                if (userEmail != null && tier != null) {
                    mongoService.updateUserTier(userEmail, tier);
                    System.out.println("✅ Tier updated for user: " + userEmail);
                } else {
                    System.out.println("⚠️ Metadata missing from webhook event.");
                }
            }

            return ResponseEntity.ok("");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body("Webhook Error");
        }
    }
}
