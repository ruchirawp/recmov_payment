package com.movieapp.paymentservice.controllers;

import com.movieapp.paymentservice.services.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/payment")
public class PlanController {

    @Autowired
    private MongoService mongoService;

    @PostMapping("/downgrade")
    public ResponseEntity<String> downgradeUser(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String tier = body.get("tier");

            if (email == null || tier == null) {
                return ResponseEntity.badRequest().body("Missing email or tier");
            }

            mongoService.updateUserTier(email, tier);
            return ResponseEntity.ok("✅ Tier downgraded successfully to " + tier);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Error while downgrading tier");
        }
    }
}
