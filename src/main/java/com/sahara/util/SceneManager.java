package com.sahara.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * SceneManager — utility to switch JavaFX scenes.
 */
public class SceneManager {

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchTo(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            primaryStage.setTitle(title);
            primaryStage.setScene(new Scene(root, 900, 600));
        } catch (IOException e) {
            System.err.println("SceneManager: cannot load " + fxmlPath + " — " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Stage getStage() {
        return primaryStage;
    }
}
