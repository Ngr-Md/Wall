package ir.aut.ap.wall.client;

import ir.aut.ap.wall.client.util.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("WALL");
        primaryStage.setMaximized(true);
        primaryStage.setFullScreenExitHint("");
        Navigator.init(primaryStage);
        Navigator.goTo("login");
        primaryStage.setFullScreen(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
