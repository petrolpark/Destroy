package com.petrolpark.destroy.badge;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.petrolpark.destroy.Destroy;

public class BadgeHandler extends WebSocketClient {

    public static String BADGES_URL = "https://petrolparkbadges.azurewebsites.net/";

    public BadgeHandler(URI serverURI) {
        super(serverURI);
    };

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Destroy.LOGGER.info("Connected to Petrolpark's Badge server.");
    };

    @Override
    public void onMessage(String message) {

    };

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Destroy.LOGGER.info("Disconneted from Petrolpark's Badge server.");
    };

    @Override
    public void onError(Exception ex) {};
    
};
