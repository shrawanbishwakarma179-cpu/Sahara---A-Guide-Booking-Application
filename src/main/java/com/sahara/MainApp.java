package com.sahara;

import com.sahara.util.DatabaseConnection;
import com.sahara.util.AlertUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * MainApp — JavaFX application entry point.
 *
 * On startup:
 *   1. Tests the database connection.
 *   2. Shows an error dialog if DB is not reachable.
 *   3. Loads the Login screen.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // ── Verify DB connection before showing any UI ──────────────────
        DatabaseConnection db = DatabaseConnection.getInstance();
        if (!db.isConnected()) {
            AlertUtil.showError(
                "Database Connection Failed",
                "SAHARA could not connect to MySQL.\n\n" +
                "Please check:\n" +
                "  1. MySQL server is running\n" +
                "  2. You have run sql/sahara_schema.sql\n" +
                "  3. Your password in src/main/resources/db.properties\n\n" +
                "The application will open but data operations will fail."
            );
        }

        // ── Load login screen ────────────────────────────────────────────
        Parent root = FXMLLoader.load(
            getClass().getResource("/com/sahara/fxml/Login.fxml"));

        primaryStage.setTitle("SAHARA - Guide Booking System");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Graceful DB shutdown on window close
        DatabaseConnection.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
