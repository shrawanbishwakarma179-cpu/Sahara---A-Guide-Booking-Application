package com.sahara.controller;

import com.sahara.dao.GuideDAO;
import com.sahara.dao.TouristDAO;
import com.sahara.model.Guide;
import com.sahara.model.Tourist;
import com.sahara.util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * RegisterController — handles Tourist and Guide registration.
 */
public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        roleCombo.getItems().addAll("Tourist", "Guide");
        roleCombo.setValue("Tourist");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String name    = nameField.getText().trim();
        String email   = emailField.getText().trim();
        String phone   = phoneField.getText().trim();
        String pass    = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        String role    = roleCombo.getValue();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Name, email and password are required.");
            return;
        }
        if (!pass.equals(confirm)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        boolean success = false;

        if ("Tourist".equals(role)) {
            Tourist t = new Tourist(0, name, email, phone, pass);
            success = new TouristDAO().register(t);
        } else {
            Guide g = new Guide(0, name, email, phone, "Available", 0.0, pass);
            success = new GuideDAO().register(g);
        }

        if (success) {
            AlertUtil.showInfo("Registered", "Account created! Please login.");
            goToLogin();
        } else {
            statusLabel.setText("Registration failed. Email may already exist.");
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        goToLogin();
    }

    private void goToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/sahara/fxml/Login.fxml"));
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("SAHARA - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
