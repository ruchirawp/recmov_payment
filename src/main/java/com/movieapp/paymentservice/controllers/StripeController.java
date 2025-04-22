package com.movieapp.paymentservice.controllers;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*") //check cors
public class StripeController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession(@RequestBody Map<String, String> request) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        String tier = request.get("tier");
        String email = request.get("email");
        String priceId = getStripePriceId(tier);

        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            // .setSuccessUrl("http://recommendmovie.onrender.com/#/payment/success")
            .setSuccessUrl("http://localhost:3000/#/payment/success")
            // .setCancelUrl("http://recommendmovie.onrender.com/#/payment/cancel")
            .setCancelUrl("http://localhost:3000/#/payment/cancel")
            .addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPrice(priceId) // Stripe dashboard price ID
                            .build()
            )
            .setClientReferenceId(email) // Store the user's email or ID
            .putMetadata("tier", tier)   // Custom data you want to read in the webhook
            .putMetadata("email", email)   // Custom data you want to read in the webhook
            .build();

        Session session = Session.create(params);

        Map<String, String> response = new HashMap<>();
        response.put("url", session.getUrl());
        return response;
    }

    private String getStripePriceId(String tier) {
        return switch (tier) {
            case "Cinema Plus" -> "price_1RG7AmHLYdmDsJssnH2SbSHb";  // Replace with real Stripe price IDs
            case "Directors Cut" -> "price_1RG7BCHLYdmDsJssdTZtH6rj";
            default -> "price_free"; // Optional or throw exception
        };
    }
}
