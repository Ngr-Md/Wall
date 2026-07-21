package ir.aut.ap.wall.client.controller;

import com.fasterxml.jackson.databind.JsonNode;
import ir.aut.ap.wall.client.util.ApiClient;
import ir.aut.ap.wall.client.util.Format;
import ir.aut.ap.wall.client.util.Navigator;
import ir.aut.ap.wall.client.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdDetailsController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label metaLabel;
    @FXML
    private Label sellerLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ImageView adImageView;
    @FXML
    private Label imageCounterLabel;
    @FXML
    private Button prevImageButton;
    @FXML
    private Button nextImageButton;
    @FXML
    private Button favoriteButton;
    @FXML
    private Button chatButton;
    @FXML
    private Button rateButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button soldButton;

    private long adId;
    private JsonNode ad;
    private int imageIndex = 0;
    private boolean isFavorite = false;

    public void load(long adId) {
        this.adId = adId;
        refresh();
    }

    private void refresh() {
        try {
            ad = ApiClient.get("/api/ads/" + adId);
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
            Navigator.goTo("home");
            return;
        }
        titleLabel.setText(ad.get("title").asText());
        priceLabel.setText(Format.price(ad.get("price").asLong()));
        metaLabel.setText(ad.get("category").get("name").asText() + " | " + ad.get("city").get("name").asText());
        JsonNode seller = ad.get("seller");
        String avg = ad.hasNonNull("sellerAverageRating")
                ? String.format(" (امتیاز: %.1f از 5)", ad.get("sellerAverageRating").asDouble())
                : " (بدون امتیاز)";
        sellerLabel.setText("فروشنده: " + seller.get("fullName").asText() + avg);
        String status = ad.get("status").asText();
        String statusText = "وضعیت: " + Format.statusFa(status);
        if ("REJECTED".equals(status) && ad.hasNonNull("rejectionReason")) {
            statusText += " — دلیل: " + ad.get("rejectionReason").asText();
        }
        statusLabel.setText(statusText);
        descriptionArea.setText(ad.get("description").asText());

        boolean isOwner = SessionManager.getCurrentUserId() != null
                && seller.get("id").asLong() == SessionManager.getCurrentUserId();
        chatButton.setVisible(!isOwner);
        rateButton.setVisible(!isOwner);
        favoriteButton.setVisible(!isOwner);
        editButton.setVisible(isOwner);
        deleteButton.setVisible(isOwner || SessionManager.isAdmin());
        soldButton.setVisible(isOwner && "APPROVED".equals(status));

        if (!isOwner) {
            try {
                isFavorite = ApiClient.get("/api/favorites/" + adId).get("favorite").asBoolean();
                favoriteButton.setText(isFavorite ? "حذف از نشان‌شده‌ها" : "نشان کردن");
            } catch (ApiClient.ApiException ignored) {
            }
        }

        imageIndex = 0;
        showImage();
    }

    private void showImage() {
        JsonNode images = ad.get("imagesBase64");
        int count = images == null ? 0 : images.size();
        prevImageButton.setDisable(count <= 1);
        nextImageButton.setDisable(count <= 1);
        if (count == 0) {
            adImageView.setImage(null);
            imageCounterLabel.setText("بدون تصویر");
            return;
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(images.get(imageIndex).asText());
            adImageView.setImage(new Image(new ByteArrayInputStream(bytes)));
            imageCounterLabel.setText((imageIndex + 1) + " از " + count);
        } catch (Exception e) {
            imageCounterLabel.setText("خطا در نمایش تصویر");
        }
    }

    @FXML
    private void onPrevImage() {
        int count = ad.get("imagesBase64").size();
        imageIndex = (imageIndex - 1 + count) % count;
        showImage();
    }

    @FXML
    private void onNextImage() {
        int count = ad.get("imagesBase64").size();
        imageIndex = (imageIndex + 1) % count;
        showImage();
    }

    @FXML
    private void onToggleFavorite() {
        try {
            if (isFavorite) {
                ApiClient.delete("/api/favorites/" + adId);
            } else {
                ApiClient.post("/api/favorites/" + adId, "");
            }
            refresh();
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
    }

    @FXML
    private void onChat() {
        try {
            JsonNode conversation = ApiClient.post("/api/conversations", Map.of("adId", adId));
            long conversationId = conversation.get("id").asLong();
            String title = conversation.get("adTitle").asText();
            Navigator.<ChatController>goTo("chat", c -> c.load(conversationId, title));
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
    }

    @FXML
    private void onRate() {
        ChoiceDialog<Integer> scoreDialog = new ChoiceDialog<>(5, 1, 2, 3, 4, 5);
        scoreDialog.setHeaderText("امتیاز شما به فروشنده (1 تا 5)");
        scoreDialog.setTitle("ثبت امتیاز");
        Navigator.prepareDialog(scoreDialog);
        Optional<Integer> score = scoreDialog.showAndWait();
        if (score.isEmpty()) {
            return;
        }
        TextInputDialog commentDialog = new TextInputDialog();
        commentDialog.setHeaderText("نظر شما (اختیاری)");
        commentDialog.setTitle("ثبت امتیاز");
        Navigator.prepareDialog(commentDialog);
        String comment = commentDialog.showAndWait().orElse("");
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("adId", adId);
            body.put("score", score.get());
            body.put("comment", comment);
            ApiClient.post("/api/ratings", body);
            Navigator.showInfo("امتیاز شما ثبت شد");
            refresh();
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        JsonNode currentAd = ad;
        Navigator.<CreateAdController>goTo("create_ad", c -> c.initEdit(currentAd));
    }

    @FXML
    private void onDelete() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "مطمئنی میخوای حذفش کنی؟؟");
        confirm.setHeaderText(null);
        Navigator.prepareDialog(confirm);
        if (confirm.showAndWait().filter(b -> b == ButtonType.OK).isPresent()) {
            try {
                ApiClient.delete("/api/ads/" + adId);
                Navigator.goTo("home");
            } catch (ApiClient.ApiException e) {
                Navigator.showError(e.getMessage());
            }
        }
    }

    @FXML
    private void onMarkSold() {
        try {
            ApiClient.post("/api/ads/" + adId + "/sold", "");
            refresh();
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
    }

    @FXML
    private void onShowRatings() {
        try {
            long sellerId = ad.get("seller").get("id").asLong();
            JsonNode data = ApiClient.get("/api/sellers/" + sellerId + "/ratings");
            JsonNode ratings = data.get("ratings");
            StringBuilder sb = new StringBuilder();
            if (ratings == null || ratings.isEmpty()) {
                sb.append("هنوز هیچکس نظری نداده...");
            } else {
                if (data.hasNonNull("average")) {
                    sb.append(String.format("میانگین امتیاز: %.1f از 5", data.get("average").asDouble()))
                            .append("\n────────────────────\n");
                }
                for (JsonNode r : ratings) {
                    sb.append("★ ").append(r.get("score").asInt()).append(" از 5")
                            .append("  —  ").append(r.get("raterUsername").asText());
                    String comment = r.hasNonNull("comment") ? r.get("comment").asText().trim() : "";
                    if (!comment.isEmpty()) {
                        sb.append("\n").append(comment);
                    }
                    sb.append("\n\n");
                }
            }
            TextArea area = new TextArea(sb.toString().trim());
            area.setEditable(false);
            area.setWrapText(true);
            area.setPrefSize(440, 320);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("نظرات فروشنده");
            alert.setHeaderText("نظرات ثبت‌شده برای این فروشنده");
            alert.getDialogPane().setContent(area);
            Navigator.prepareDialog(alert);
            alert.showAndWait();
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        Navigator.goTo("home");
    }
}
