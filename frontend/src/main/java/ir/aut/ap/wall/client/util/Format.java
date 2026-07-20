package ir.aut.ap.wall.client.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class Format {

    private Format() {
    }

    public static String price(long price) {
        return NumberFormat.getInstance(Locale.US).format(price) + " چندرغاز";
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
