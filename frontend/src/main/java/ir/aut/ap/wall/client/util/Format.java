package ir.aut.ap.wall.client.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class Format {

    private Format() {
    }

    public static String price(long price) {
        return NumberFormat.getInstance(Locale.US).format(price) + " چندرغاز";
    }

    private static final java.time.format.DateTimeFormatter MESSAGE_TIME =
            java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
                    .withZone(java.time.ZoneId.systemDefault());

    public static String messageTime(String isoInstant) {
        try {
            return MESSAGE_TIME.format(java.time.Instant.parse(isoInstant));
        } catch (Exception e) {
            return "";
        }
    }

    public static String statusFa(String status) {
        return switch (status) {
            case "PENDING" -> "هنوز تایید نشده برار...";
            case "APPROVED" -> "تایید شده، زنده باد!";
            case "REJECTED" -> "رد شده، افسوس!";
            case "SOLD" -> "فروخته شده، هورااا!";
            default -> status;
        };
    }
}
