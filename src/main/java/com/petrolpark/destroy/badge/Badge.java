package com.petrolpark.destroy.badge;

import java.util.HashSet;
import java.util.Set;

import org.bson.BsonDocument;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.petrolpark.destroy.Destroy;

public class Badge {

    private static final int CURRENT_VERSION = 0;
    private static final String connectionString = "mongodb+srv://destroy_mod_client:ljHgenh5d4LbRJ3j@destroybadges.9onlnbb.mongodb.net/?retryWrites=true&w=majority";

    public Badge() {};

    public static Set<Badge> getBadgesOf(String playerName) {
        Set<Badge> badges = new HashSet<>();

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("destroy");
            MongoCollection<Document> collection = database.getCollection("badges");
            Document document = collection.find(Filters.eq("playername", playerName)).first();
            if (document != null) {
                document.getList("badges", String.class).forEach(badgeId -> {
                    DestroyBadges.getBadgeFromId(Destroy.asResource(badgeId)).ifPresent(badge -> badges.add(badge));
                });
            };
            database.
        } catch (Error e) {};
        Destroy.LOGGER.info("Ive got "+badges);
        return badges;
    };
};
