package ir.aut.ap.wall.client.controller;

import com.fasterxml.jackson.databind.JsonNode;
import ir.aut.ap.wall.client.util.ApiClient;
import ir.aut.ap.wall.client.util.Navigator;
import ir.aut.ap.wall.client.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Map;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    private void onLogin() {
        errorLabel.setText("");
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("یوزرنیم و رمزت رو بزن لطفا");
            return;
        }
        try {
            JsonNode response = ApiClient.post("/api/auth/login",
                    Map.of("username", username, "password", password));
            SessionManager.startSession(response.get("token").asText(), response.get("user"));
            Navigator.goTo("home");
        } catch (ApiClient.ApiException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onGoToRegister() {
        Navigator.goTo("register");
    }
}
