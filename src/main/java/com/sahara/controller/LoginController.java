package com.sahara.controller;

import com.sahara.dao.AdminDAO;
import com.sahara.dao.GuideDAO;
import com.sahara.dao.TouristDAO;
import com.sahara.model.Admin;
import com.sahara.model.Guide;
import com.sahara.model.Tourist;
import com.sahara.util.AlertUtil;
import com.sahara.util.SceneManager;
import com.sahara.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * LoginController — handles login for Tourist, Guide and Admin.
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        roleCombo.getItems().addAll("Tourist", "Guide", "Admin");
        roleCombo.setValue("Tourist");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String role     = roleCombo.getValue();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter email and password.");
            return;
        }

        Session session = Session.getInstance();

        switch (role) {
            case "Tourist": {
                TouristDAO dao = new TouristDAO();
                Tourist tourist = dao.login(email, password);
                if (tourist != null) {
                    session.setCurrentUser(tourist);
                    session.setCurrentRole("Tourist");
                    loadScene("/com/sahara/fxml/TouristPortal.fxml", "SAHARA - Tourist Portal");
                } else {
                    statusLabel.setText("Invalid email or password.");
                }
                break;
            }
            case "Guide": {
                GuideDAO dao = new GuideDAO();
                Guide guide = dao.login(email, password);
                if (guide != null) {
                    session.setCurrentUser(guide);
                    session.setCurrentRole("Guide");
                    loadScene("/com/sahara/fxml/GuidePortal.fxml", "SAHARA - Guide Portal");
                } else {
                    statusLabel.setText("Invalid email or password.");
                }
                break;
            }
            case "Admin": {
                AdminDAO dao = new AdminDAO();
                Admin admin = dao.login(email, password);
                if (admin != null) {
                    session.setCurrentUser(admin);
                    session.setCurrentRole("Admin");
                    loadScene("/com/sahara/fxml/AdminPortal.fxml", "SAHARA - Admin Portal");
                } else {
                    statusLabel.setText("Invalid email or password.");
                }
                break;
            }
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        loadScene("/com/sahara/fxml/Register.fxml", "SAHARA - Register");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle(title);
        } catch (Exception e) {
            AlertUtil.showError("Navigation Error", "Cannot load screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
