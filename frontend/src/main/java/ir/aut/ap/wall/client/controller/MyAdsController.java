package ir.aut.ap.wall.client.controller;

import com.fasterxml.jackson.databind.JsonNode;
import ir.aut.ap.wall.client.util.ApiClient;
import ir.aut.ap.wall.client.util.Format;
import ir.aut.ap.wall.client.util.Navigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class MyAdsController {

    @FXML
    private ListView<JsonNode> myAdsListView;

    @FXML
    private void initialize() {
        myAdsListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(JsonNode ad, boolean empty) {
                super.updateItem(ad, empty);
                if (empty || ad == null) {
                    setText(null);
                } else {
                    setText(ad.get("title").asText()
                            + "  |  " + Format.price(ad.get("price").asLong())
                            + "  |  وضعیت: " + Format.statusFa(ad.get("status").asText()));
                }
            }
        });
        myAdsListView.setOnMouseClicked(event -> {
            JsonNode selected = myAdsListView.getSelectionModel().getSelectedItem();
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
            ApiClient.get("/api/ads/mine").forEach(items::add);
            myAdsListView.setItems(items);
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        Navigator.goTo("home");
    }
}
