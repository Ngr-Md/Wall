package ir.aut.ap.wall.client.controller;

import com.fasterxml.jackson.databind.JsonNode;
import ir.aut.ap.wall.client.util.ApiClient;
import ir.aut.ap.wall.client.util.Navigator;
import ir.aut.ap.wall.client.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class ConversationsController {

    @FXML
    private ListView<JsonNode> conversationsListView;

    @FXML
    private void initialize() {
        conversationsListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(JsonNode conversation, boolean empty) {
                super.updateItem(conversation, empty);
                if (empty || conversation == null) {
                    setText(null);
                    return;
                }
                boolean amBuyer = conversation.get("buyer").get("id").asLong() == SessionManager.getCurrentUserId();
                String otherUser = amBuyer
                        ? conversation.get("seller").get("fullName").asText()
                        : conversation.get("buyer").get("fullName").asText();
                long unread = conversation.get("unreadCount").asLong();
                setText("آگهی: " + conversation.get("adTitle").asText()
                        + "  |  طرف گفتگو: " + otherUser
                        + (unread > 0 ? "  |  " + unread + " پیام سین نزده" : ""));
            }
        });
        conversationsListView.setOnMouseClicked(event -> {
            JsonNode selected = conversationsListView.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2 && selected != null) {
                long id = selected.get("id").asLong();
                String title = selected.get("adTitle").asText();
                Navigator.<ChatController>goTo("chat", c -> c.load(id, title));
            }
        });
        onRefresh();
    }

    @FXML
    private void onRefresh() {
        try {
            var items = FXCollections.<JsonNode>observableArrayList();
            ApiClient.get("/api/conversations").forEach(items::add);
            conversationsListView.setItems(items);
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        Navigator.goTo("home");
    }
}
