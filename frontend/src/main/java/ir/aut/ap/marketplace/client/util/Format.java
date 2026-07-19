package ir.aut.ap.marketplace.client.util;

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
            case "PENDING" -> "در انتظار تایید";
            case "APPROVED" -> "تایید شده";
            case "REJECTED" -> "رد شده";
            case "SOLD" -> "فروخته شده";
            default -> status;
        };
    }
}
