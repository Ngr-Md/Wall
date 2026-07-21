package ir.aut.ap.wall.client.controller;

import com.fasterxml.jackson.databind.JsonNode;
import ir.aut.ap.wall.client.util.ApiClient;
import ir.aut.ap.wall.client.util.Format;
import ir.aut.ap.wall.client.util.Navigator;
import ir.aut.ap.wall.client.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.Map;

public class ChatController {

    @FXML
    private Label headerLabel;
    @FXML
    private ListView<JsonNode> messagesListView;
    @FXML
    private TextField messageField;

    private long conversationId;

    @FXML
    private void initialize() {
        messagesListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(JsonNode message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                boolean mine = message.get("senderUsername").asText().equals(SessionManager.getCurrentUsername());

                Label messageLabel = new Label(
                        (mine ? "من: " : message.get("senderUsername").asText() + ": ")
                                + message.get("content").asText());
                messageLabel.setWrapText(true);

                Label timeLabel = new Label(Format.messageTime(message.get("sentAt").asText()));
                timeLabel.setStyle("-fx-text-fill: #8a8a8a; -fx-font-size: 11px;");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox row = new HBox(8, messageLabel, spacer, timeLabel);
                row.setAlignment(Pos.CENTER_LEFT);

                setText(null);
                setGraphic(row);
            }
        });
    }

    public void load(long conversationId, String adTitle) {
        this.conversationId = conversationId;
        headerLabel.setText("موضوع چت: " + adTitle);
        onRefresh();
    }

    @FXML
    private void onRefresh() {
        try {
            var items = FXCollections.<JsonNode>observableArrayList();
            ApiClient.get("/api/conversations/" + conversationId + "/messages").forEach(items::add);
            messagesListView.setItems(items);
            if (!items.isEmpty()) {
                messagesListView.scrollTo(items.size() - 1);
            }
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
    }

    @FXML
    private void onSend() {
        String content = messageField.getText().trim();
        if (content.isEmpty()) {
            return;
        }
        try {
            ApiClient.post("/api/conversations/" + conversationId + "/messages", Map.of("content", content));
            messageField.clear();
            onRefresh();
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        Navigator.goTo("conversations");
    }
}
