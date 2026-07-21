package ir.aut.ap.wall.client.controller;

import com.fasterxml.jackson.databind.JsonNode;
import ir.aut.ap.wall.client.util.ApiClient;
import ir.aut.ap.wall.client.util.Navigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAdController {

    @FXML
    private Label headerLabel;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<JsonNode> categoryCombo;
    @FXML
    private ComboBox<JsonNode> cityCombo;
    @FXML
    private Label imagesLabel;
    @FXML
    private Label errorLabel;

    private final List<String> imagesBase64 = new ArrayList<>();
    private Long editingAdId;

    @FXML
    private void initialize() {
        setupNamedCombo(categoryCombo);
        setupNamedCombo(cityCombo);
        try {
            ApiClient.get("/api/meta/categories").forEach(categoryCombo.getItems()::add);
            ApiClient.get("/api/meta/cities").forEach(cityCombo.getItems()::add);
        } catch (ApiClient.ApiException e) {
            Navigator.showError(e.getMessage());
        }
        updateImagesLabel();
    }

    public void initCreate() {
        editingAdId = null;
        headerLabel.setText("آگهی جدید بذار!!");
    }

    public void initEdit(JsonNode ad) {
        editingAdId = ad.get("id").asLong();
        headerLabel.setText("ویرایش آگهی");
        titleField.setText(ad.get("title").asText());
        descriptionArea.setText(ad.get("description").asText());
        priceField.setText(String.valueOf(ad.get("price").asLong()));
        selectById(categoryCombo, ad.get("category").get("id").asLong());
        selectById(cityCombo, ad.get("city").get("id").asLong());
        imagesBase64.clear();
        if (ad.has("imagesBase64")) {
            ad.get("imagesBase64").forEach(node -> imagesBase64.add(node.asText()));
        }
        updateImagesLabel();
    }

    private void selectById(ComboBox<JsonNode> combo, long id) {
        combo.getItems().stream()
                .filter(item -> item.get("id").asLong() == id)
                .findFirst()
                .ifPresent(item -> combo.getSelectionModel().select(item));
    }

    private void setupNamedCombo(ComboBox<JsonNode> combo) {
        combo.setItems(FXCollections.observableArrayList());
        ListCell<JsonNode> buttonCell = namedCell();
        combo.setButtonCell(buttonCell);
        combo.setCellFactory(list -> namedCell());
    }

    private ListCell<JsonNode> namedCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(JsonNode item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.get("name").asText());
            }
        };
    }

    @FXML
    private void onAddImage() {
        if (imagesBase64.size() >= 5) {
            errorLabel.setText("حداکثر 5 تصویر مجاز است");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("انتخاب تصویر");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("تصاویر", "*.png", "*.jpg", "*.jpeg"));
        File file = chooser.showOpenDialog(titleField.getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            if (file.length() > 2 * 1024 * 1024) {
                errorLabel.setText("سرور ارزان نیست، لطفا حجم هر عکس حداکثر 2 مگ باشه");
                return;
            }
            imagesBase64.add(Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath())));
            errorLabel.setText("");
            updateImagesLabel();
        } catch (Exception e) {
            errorLabel.setText("فایلت یه مشکلی داره");
        }
    }

    @FXML
    private void onClearImages() {
        imagesBase64.clear();
        updateImagesLabel();
    }

    private void updateImagesLabel() {
        imagesLabel.setText(imagesBase64.size() + " عکس بارگذاری کردی");
    }

    @FXML
    private void onSubmit() {
        errorLabel.setText("");
        if (titleField.getText().trim().length() < 3) {
            errorLabel.setText("عنوانت خیلی کوتاهه");
            return;
        }
        if (descriptionArea.getText().trim().length() < 10) {
            errorLabel.setText("خجالت نکش، بیشتر توضیح بده");
            return;
        }
        long price;
        try {
            price = Long.parseLong(priceField.getText().trim());
            if (price < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("قیمت باید عدد صحیح غیرمنفی باشد");
            return;
        }
        if (categoryCombo.getValue() == null || cityCombo.getValue() == null) {
            errorLabel.setText("دسته‌بندی و شهر را انتخاب کنید");
            return;
        }
        Map<String, Object> body = new HashMap<>();
        body.put("title", titleField.getText().trim());
        body.put("description", descriptionArea.getText().trim());
        body.put("price", price);
        body.put("categoryId", categoryCombo.getValue().get("id").asLong());
        body.put("cityId", cityCombo.getValue().get("id").asLong());
        body.put("imagesBase64", imagesBase64);
        try {
            if (editingAdId == null) {
                ApiClient.post("/api/ads", body);
                Navigator.showInfo("آگهیت ثبت شد، اگر ادمین لطف کنه تایید میشه");
            } else {
                ApiClient.put("/api/ads/" + editingAdId, body);
                Navigator.showInfo("حالا که عوضش کردی باید دوباره صبر کنیم ادمین تایید کنه");
            }
            Navigator.goTo("home");
        } catch (ApiClient.ApiException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        Navigator.goTo("home");
    }
}
