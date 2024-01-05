package com.petrolpark.destroy.badge;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.capability.player.PlayerBadges;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.world.entity.player.Player;

public class BadgeHandler {

    public static final String VERSION_UUID = "6c922047-75ea-4e39-b452-dc41964ad98e";

    public static final String GET_BADGES_URL = "https://us-central1.gcp.data.mongodb-api.com/app/destroybadges-qojlw/endpoint/GetBadges";

    public static final String EARLY_BIRD_URL = "https://us-central1.gcp.data.mongodb-api.com/app/destroybadges-qojlw/endpoint/AddEarlyBird";
    public static final boolean EARLY_BIRD_VIABLE = true; // Whether the Early Bird badge is still available in this release

    public static void getAndAddBadges(Player player) {
        HttpClient client = HttpClient.newHttpClient();

        String getBadgesJsonInputString = "{\"playername\": \""+player.getScoreboardName()+"\"}";

        HttpRequest getBadgesRequest = HttpRequest.newBuilder()
                .uri(URI.create(GET_BADGES_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(getBadgesJsonInputString))
                .build();

        CompletableFuture<HttpResponse<InputStream>> responseFuture = client.sendAsync(getBadgesRequest, HttpResponse.BodyHandlers.ofInputStream());

        responseFuture.thenAcceptAsync(response -> {
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()));
            ) {
                List<Pair<Badge, Date>> badges = new ArrayList<>();
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (JsonElement element : json.getAsJsonArray("badges")) {
                    JsonObject badgeObject = element.getAsJsonObject();
                    String date = badgeObject.get("date").getAsString();
                    date = date.substring(0, date.length() - 1);
                    Badge badge = Badge.getBadge(badgeObject.get("namespace").getAsString(), badgeObject.get("id").getAsString());
                    Destroy.LOGGER.info(badgeObject.get("namespace").getAsString(), badgeObject.get("id").getAsString());
                    if (badge != null) {
                        badges.add(Pair.of(
                            badge,
                            Date.from(LocalDateTime.parse(date).toInstant(ZoneOffset.UTC))
                        ));
                    };
                };
                player.getCapability(PlayerBadges.Provider.PLAYER_BADGES).ifPresent(playerBadges -> {
                    playerBadges.setBadges(badges);
                    // Award Advancements for Badges
                    playerBadges.getBadges().forEach(pair ->
                        pair.getFirst().grantAdvancement(player));
                });
            } catch (Exception e) {};
        }).join();

    };

    public static void fetchAndAddBadgesIncludingEarlyBird(Player player) {
        try {
            if (EARLY_BIRD_VIABLE) { // Try and give this Player the Early bird badge
                HttpClient client = HttpClient.newHttpClient();

                String addEarlyBirdJsonInputString = "{\"playername\": \""+player.getScoreboardName()+"\", \"version_uuid\": \""+VERSION_UUID+"\"}";

                HttpRequest getBadgesRequest = HttpRequest.newBuilder()
                    .uri(URI.create(EARLY_BIRD_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(addEarlyBirdJsonInputString))
                    .build();

                CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(getBadgesRequest, HttpResponse.BodyHandlers.ofString());

                responseFuture.thenAcceptAsync(response -> {
                    getAndAddBadges(player);
                }).join();
            
            } else { // If Early Bird is no longer obtainable, get the Badges straight away
                getAndAddBadges(player);
            };
        } catch (Throwable e) {
            Destroy.LOGGER.error("Error fetching Badges for player: ", e);
        };

    };
    
};
