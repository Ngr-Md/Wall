package ir.aut.ap.wall.client.util;

import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

import java.util.function.Consumer;

public final class Navigator {

    private static Stage primaryStage;
    private static Scene scene;

    private Navigator() {
    }

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void goTo(String fxmlName) {
        goTo(fxmlName, null);
    }

    public static <T> void goTo(String fxmlName, Consumer<T> controllerInitializer) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource("/fxml/" + fxmlName + ".fxml"));
            Parent root = loader.load();
            root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            if (controllerInitializer != null) {
                controllerInitializer.accept(loader.getController());
            }
            if (scene == null) {
                scene = new Scene(root, 1000, 680);
                var css = Navigator.class.getResource("/css/style.css");
                if (css != null) {
                    scene.getStylesheets().add(css.toExternalForm());
                }
                primaryStage.setScene(scene);
                primaryStage.show();
            } else {
                scene.setRoot(root);
            }
        } catch (Exception e) {
            showError("خطا در بارگذاری صفحه " + fxmlName + ": " + e.getMessage());
        }
    }

    public static void prepareDialog(Dialog<?> dialog) {
        if (primaryStage != null && primaryStage.isShowing()) {
            dialog.initOwner(primaryStage);
        }
        dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        var css = Navigator.class.getResource("/css/style.css");
        if (css != null) {
            dialog.getDialogPane().getStylesheets().add(css.toExternalForm());
        }
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText("خطا");
        prepareDialog(alert);
        alert.showAndWait();
    }

    public static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        prepareDialog(alert);
        alert.showAndWait();
    }
}
