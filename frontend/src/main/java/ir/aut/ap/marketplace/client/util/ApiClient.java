package ir.aut.ap.marketplace.client.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.aut.ap.marketplace.client.config.ApiConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class ApiClient {

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final ObjectMapper mapper = new ObjectMapper();

    private ApiClient() {
    }

    public static class ApiException extends RuntimeException {
        private final int statusCode;

        public ApiException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

    public static JsonNode get(String path) {
        return send(request(path).GET().build());
    }

    public static JsonNode post(String path, Object body) {
        return send(request(path)
                .POST(HttpRequest.BodyPublishers.ofString(toJson(body), StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .build());
    }

    public static JsonNode put(String path, Object body) {
        return send(request(path)
                .PUT(HttpRequest.BodyPublishers.ofString(toJson(body), StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .build());
    }

    public static JsonNode delete(String path) {
        return send(request(path).DELETE().build());
    }

    private static HttpRequest.Builder request(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + path))
                .timeout(Duration.ofSeconds(20));
        if (SessionManager.isLoggedIn()) {
            builder.header("Authorization", "Bearer " + SessionManager.getToken());
        }
        return builder;
    }

    private static String toJson(Object body) {
        try {
            return body instanceof String s ? s : mapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException("خطا در ساخت JSON", e);
        }
    }

    private static JsonNode send(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String body = response.body();
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return (body == null || body.isBlank()) ? mapper.nullNode() : mapper.readTree(body);
            }
            String message = "خطای سرور (" + response.statusCode() + ")";
            if (body != null && !body.isBlank()) {
                try {
                    JsonNode node = mapper.readTree(body);
                    if (node.hasNonNull("message")) {
                        message = node.get("message").asText();
                    }
                } catch (Exception ignored) {
                }
            }
            throw new ApiException(response.statusCode(), message);
        } catch (ApiException e) {
            throw e;
        } catch (java.net.ConnectException e) {
            throw new ApiException(0, "اتصال به سرور برقرار نشد. مطمئن شوید Backend روی پورت 8080 اجرا است.");
        } catch (Exception e) {
            throw new ApiException(0, "خطا در ارتباط با سرور: " + e.getMessage());
        }
    }
}
