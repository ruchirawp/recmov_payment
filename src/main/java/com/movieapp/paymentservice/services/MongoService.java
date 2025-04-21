package com.movieapp.paymentservice.services;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MongoService {

    @Value("${mongo.uri}")
    private String mongoUri;

    private static final String DATABASE_NAME = "rec-mov";
    private static final String COLLECTION_NAME = "users";

    private MongoDatabase database;

    @PostConstruct
    public void init() {
        MongoClient mongoClient = MongoClients.create(mongoUri);
        this.database = mongoClient.getDatabase(DATABASE_NAME);
    }

    public void updateUserTier(String email, String tier) {
        try {
            database.getCollection(COLLECTION_NAME)
                    .updateOne(Filters.eq("email", email), Updates.set("tier", tier));
            System.out.println("✅ Tier updated for user: " + email);
        } catch (Exception e) {
            System.err.println("❌ Failed to update tier: " + e.getMessage());
        }
    }
}
