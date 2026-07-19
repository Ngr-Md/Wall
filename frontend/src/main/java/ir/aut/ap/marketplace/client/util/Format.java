package ir.aut.ap.marketplace.client.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class Format {

    private Format() {
    }

    public static String price(long price) {
        return NumberFormat.getInstance(Locale.US).format(price) + " تومان";
    }

    public static String statusFa(String status) {
        return switch (status) {
            case "PENDING" -> "در انتظار تایید";
            case "APPROVED" -> "تاییدشده";
            case "REJECTED" -> "ردشده";
            case "SOLD" -> "فروخته‌شده";
            default -> status;
        };
    }
}
