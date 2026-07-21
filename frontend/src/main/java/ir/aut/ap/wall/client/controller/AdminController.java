package ir.aut.ap.wall.client.controller;

import com.fasterxml.jackson.databind.JsonNode;
import ir.aut.ap.wall.client.util.ApiClient;
import ir.aut.ap.wall.client.util.Format;
import ir.aut.ap.wall.client.util.Navigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;

import java.util.Map;
import java.util.Optional;

public class AdminController {

    @FXML
    private ListView<JsonNode> pendingListView;

    @FXML
    private void initialize() {
        pendingListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(JsonNode ad, boolean empty) {
                super.updateItem(ad, empty);
                if (empty || ad == null) {
                    setText(null);
                } else {
                    setText(ad.get("title").asText()
                            + "  |  " + Format.price(ad.get("price").asLong())
                            + "  |  " + ad.get("category").asText()
                            + "  |  فروشنده: " + ad.get("sellerUsername").asText());
                }
            }
        });
        pendingListView.setOnMouseClicked(event -> {
            JsonNode selected = pendingListView.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2 && selected != null) {
                long adId = selected.get("id").asLong();
                Navigator.<AdDetailsController>goTo("ad_details", c -> c.load(adId));
            }
        });
        onRefresh();
    }

    @FXML
    private void onRefresh() {
        try {
            var items = FXCollections.<JsonNode>observableArrayList();
            ApiClient.get("/api/admin/ads/pending").forEach(items::add);
            pendingListView.setItems(items);
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
    }

    @FXML
    private void onApprove() {
        JsonNode selected = pendingListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Navigator.showError("اول یک آگهی انتخاب کن");
            return;
        }
        try {
            ApiClient.post("/api/admin/ads/" + selected.get("id").asLong() + "/review",
                    Map.of("approve", true));
            Navigator.showInfo("آگهی تایید شد");
            onRefresh();
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
    }

    @FXML
    private void onReject() {
        JsonNode selected = pendingListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Navigator.showError("اول یک آگهی انتخاب کن");
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("رد آگهی");
        dialog.setHeaderText("چرا آگهی رد شد؟");
        Navigator.prepareDialog(dialog);
        Optional<String> reason = dialog.showAndWait();
        if (reason.isEmpty() || reason.get().isBlank()) {
            return;
        }
        try {
            ApiClient.post("/api/admin/ads/" + selected.get("id").asLong() + "/review",
                    Map.of("approve", false, "reason", reason.get().trim()));
            Navigator.showInfo("آگهی رد شد");
            onRefresh();
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        Navigator.goTo("home");
    }
}
