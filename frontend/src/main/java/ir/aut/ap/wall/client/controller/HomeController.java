package ir.aut.ap.wall.client.controller;

import com.fasterxml.jackson.databind.JsonNode;
import ir.aut.ap.wall.client.util.ApiClient;
import ir.aut.ap.wall.client.util.Format;
import ir.aut.ap.wall.client.util.Navigator;
import ir.aut.ap.wall.client.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class HomeController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<JsonNode> categoryCombo;
    @FXML
    private ComboBox<JsonNode> cityCombo;
    @FXML
    private TextField minPriceField;
    @FXML
    private TextField maxPriceField;
    @FXML
    private ComboBox<String> sortCombo;
    @FXML
    private FlowPane adsFlowPane;
    @FXML
    private Button adminButton;

    @FXML
    private void initialize() {
        welcomeLabel.setText("خوش اومدی، " + SessionManager.getCurrentUsername());
        adminButton.setVisible(SessionManager.isAdmin());
        adminButton.setManaged(SessionManager.isAdmin());

        sortCombo.setItems(FXCollections.observableArrayList("جدید ترین", "قدیمی ترین", "ارزون ترین", "گرون ترین"));
        sortCombo.getSelectionModel().selectFirst();

        setupNamedCombo(categoryCombo, "همه ی دسته‌ها");
        setupNamedCombo(cityCombo, "همه ی شهرها");

        try {
            categoryCombo.getItems().addAll(toList(ApiClient.get("/api/meta/categories")));
            cityCombo.getItems().addAll(toList(ApiClient.get("/api/meta/cities")));
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
        onSearch();
    }

    private void setupNamedCombo(ComboBox<JsonNode> combo, String allLabel) {
        combo.setButtonCell(namedCell(allLabel));
        combo.setCellFactory(list -> namedCell(allLabel));
        combo.getItems().add(null);
        combo.getSelectionModel().selectFirst();
    }

    private ListCell<JsonNode> namedCell(String allLabel) {
        return new ListCell<>() {
            @Override
            protected void updateItem(JsonNode item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item == null ? allLabel : item.get("name").asText());
            }
        };
    }

    private List<JsonNode> toList(JsonNode array) {
        List<JsonNode> items = new ArrayList<>();
        array.forEach(items::add);
        return items;
    }

    @FXML
    private void onSearch() {
        try {
            StringBuilder path = new StringBuilder("/api/ads?sort=").append(sortParam());
            String q = searchField.getText().trim();
            if (!q.isEmpty()) {
                path.append("&q=").append(URLEncoder.encode(q, StandardCharsets.UTF_8));
            }
            JsonNode category = categoryCombo.getValue();
            if (category != null) {
                path.append("&categoryId=").append(category.get("id").asLong());
            }
            JsonNode city = cityCombo.getValue();
            if (city != null) {
                path.append("&cityId=").append(city.get("id").asLong());
            }
            appendPrice(path, "minPrice", minPriceField);
            appendPrice(path, "maxPrice", maxPriceField);
            renderAds(toList(ApiClient.get(path.toString())));
        } catch (NumberFormatException e) {
            Navigator.showError("میدونم سخته، ولی باید عدد گذاشت روش!");
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
    }

    private void renderAds(List<JsonNode> ads) {
        adsFlowPane.getChildren().clear();
        if (ads.isEmpty()) {
            Label empty = new Label("متاسفانه آگهی ای نیست");
            empty.getStyleClass().add("subtitle-label");
            adsFlowPane.getChildren().add(empty);
            return;
        }
        for (JsonNode ad : ads) {
            adsFlowPane.getChildren().add(buildAdCard(ad));
        }
    }

    private VBox buildAdCard(JsonNode ad) {
        VBox card = new VBox(8);
        card.getStyleClass().add("ad-card");
        card.setPrefWidth(270);
        card.setMinWidth(270);
        card.setMaxWidth(270);

        StackPane imageBox = new StackPane();
        imageBox.getStyleClass().add("ad-card-image");
        imageBox.setPrefSize(250, 170);
        imageBox.setMinHeight(170);
        imageBox.setMaxHeight(170);
        if (ad.hasNonNull("firstImageBase64")) {
            try {
                byte[] bytes = Base64.getDecoder().decode(ad.get("firstImageBase64").asText());
                ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(bytes)));
                imageView.setFitWidth(250);
                imageView.setFitHeight(170);
                imageView.setPreserveRatio(true);
                imageBox.getChildren().add(imageView);
            } catch (Exception ignored) {
            }
        }
        if (imageBox.getChildren().isEmpty()) {
            Label placeholder = new Label("📷");
            placeholder.setStyle("-fx-font-size: 46px;");
            imageBox.getChildren().add(placeholder);
        }

        Label title = new Label(ad.get("title").asText());
        title.getStyleClass().add("ad-card-title");
        title.setWrapText(true);

        Label price = new Label(Format.price(ad.get("price").asLong()));
        price.getStyleClass().add("price-label");

        Label categoryChip = new Label(ad.get("category").asText());
        categoryChip.getStyleClass().add("chip");
        Label cityChip = new Label(ad.get("city").asText());
        cityChip.getStyleClass().add("chip");
        HBox chips = new HBox(6, categoryChip, cityChip);
        chips.setAlignment(Pos.CENTER_LEFT);

        Label seller = new Label("فروشنده: " + ad.get("sellerUsername").asText());
        seller.getStyleClass().add("ad-card-seller");

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        card.getChildren().addAll(imageBox, title, price, chips, spacer, seller);

        long adId = ad.get("id").asLong();
        card.setOnMouseClicked(event -> Navigator.<AdDetailsController>goTo("ad_details", c -> c.load(adId)));
        return card;
    }

    private void appendPrice(StringBuilder path, String param, TextField field) {
        String text = field.getText().trim();
        if (!text.isEmpty()) {
            path.append("&").append(param).append("=").append(Long.parseLong(text));
        }
    }

    private String sortParam() {
        return switch (sortCombo.getValue() == null ? "" : sortCombo.getValue()) {
            case "قدیمی ترین" -> "oldest";
            case "ارزون ترین" -> "price_asc";
            case "گرون ترین" -> "price_desc";
            default -> "newest";
        };
    }

    @FXML
    private void onCreateAd() {
        Navigator.<CreateAdController>goTo("create_ad", c -> c.initCreate());
    }

    @FXML
    private void onMyAds() {
        Navigator.goTo("my_ads");
    }

    @FXML
    private void onFavorites() {
        Navigator.goTo("favorites");
    }

    @FXML
    private void onConversations() {
        Navigator.goTo("conversations");
    }

    @FXML
    private void onAdmin() {
        Navigator.goTo("admin");
    }

    @FXML
    private void onLogout() {
        SessionManager.clear();
        Navigator.goTo("login");
    }
}
