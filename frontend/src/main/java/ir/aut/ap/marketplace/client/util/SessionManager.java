package ir.aut.ap.marketplace.client.util;

import com.fasterxml.jackson.databind.JsonNode;

public final class SessionManager {

    private static String token;
    private static JsonNode currentUser;

    private SessionManager() {
    }

    public static void startSession(String jwtToken, JsonNode user) {
        token = jwtToken;
        currentUser = user;
    }

    public static void clear() {
        token = null;
        currentUser = null;
    }

    public static String getToken() {
        return token;
    }

    public static boolean isLoggedIn() {
        return token != null;
    }

    public static JsonNode getCurrentUser() {
        return currentUser;
    }

    public static Long getCurrentUserId() {
        return currentUser == null ? null : currentUser.get("id").asLong();
    }

    public static String getCurrentUsername() {
        return currentUser == null ? null : currentUser.get("username").asText();
    }

    public static boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.get("role").asText());
    }
}
