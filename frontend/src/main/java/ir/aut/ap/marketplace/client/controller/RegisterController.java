package ir.aut.ap.marketplace.client.controller;

import com.fasterxml.jackson.databind.JsonNode;
import ir.aut.ap.marketplace.client.util.ApiClient;
import ir.aut.ap.marketplace.client.util.Navigator;
import ir.aut.ap.marketplace.client.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private Label errorLabel;

    @FXML
    private void onRegister() {
        errorLabel.setText("");
        if (usernameField.getText().trim().length() < 4) {
            errorLabel.setText("یوزرنیمت باید حداقل 4 حرف باشه داداش");
            return;
        }
        if (passwordField.getText().length() < 8) {
            errorLabel.setText("رمزت باید حداقل 8 حرف باشه داداش");
            return;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errorLabel.setText("رمز و تکرارش یکی نیستن الدنگ");
            return;
        }
        if (fullNameField.getText().trim().isEmpty()) {
            errorLabel.setText("نام رو کامل وارد کن");
            return;
        }
        if (!emailField.getText().trim().matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$")) {
            errorLabel.setText("ایمیل درست نیست");
            return;
        }
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("^09\\d{9}$")) {
            errorLabel.setText("شماره موبایل باید با 09 شروع شود و 11 رقم باشد");
            return;
        }
        try {
            Map<String, String> body = new HashMap<>();
            body.put("username", usernameField.getText().trim());
            body.put("password", passwordField.getText());
            body.put("fullName", fullNameField.getText().trim());
            body.put("email", emailField.getText().trim());
            body.put("phone", phone);
            JsonNode response = ApiClient.post("/api/auth/register", body);
            SessionManager.startSession(response.get("token").asText(), response.get("user"));
            Navigator.goTo("home");
        } catch (ApiClient.ApiException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onGoToLogin() {
        Navigator.goTo("login");
    }
}
