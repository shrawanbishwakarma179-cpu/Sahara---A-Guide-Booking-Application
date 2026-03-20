package com.sahara.controller;

import com.sahara.dao.BookingDAO;
import com.sahara.dao.FeedbackDAO;
import com.sahara.dao.GuideDAO;
import com.sahara.model.Booking;
import com.sahara.model.Feedback;
import com.sahara.model.Guide;
import com.sahara.util.Session;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GuidePortalController {

    @FXML private Label welcomeLabel;
    @FXML private Label ratingLabel;

    // ---- Bookings Tab ----
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Integer> colBId;
    @FXML private TableColumn<Booking, String> colTourist;
    @FXML private TableColumn<Booking, LocalDateTime> colDate;
    @FXML private TableColumn<Booking, String> colStatus;
    @FXML private TableColumn<Booking, String> colPayment;

    // ---- Profile Tab ----
    @FXML private TextField guideNameField;
    @FXML private TextField guidePhoneField;
    @FXML private ComboBox<String> availabilityCombo;
    @FXML private Label profileStatus;

    // ---- Reviews Tab ----
    @FXML private TableView<Feedback> reviewsTable;
    @FXML private TableColumn<Feedback, String> colReviewer;
    @FXML private TableColumn<Feedback, Integer> colRating;
    @FXML private TableColumn<Feedback, String> colComment;
    @FXML private TableColumn<Feedback, String> colReviewDate;

    private final BookingDAO bookingDAO = new BookingDAO();
    private final GuideDAO guideDAO = new GuideDAO();
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    private Guide currentGuide;

    // ✅ Formatter for date + time
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        currentGuide = (Guide) Session.getInstance().getCurrentUser();

        welcomeLabel.setText("Welcome, " + currentGuide.getName() + "!");
        ratingLabel.setText("Your Rating: " + currentGuide.getRating() + " / 5.0");

        setupBookingsTable();
        setupProfileTab();
        setupReviewsTable();
    }

    // ===================== BOOKINGS TAB =====================

    private void setupBookingsTable() {

        colBId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colTourist.setCellValueFactory(new PropertyValueFactory<>("touristName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        // ✅ Real-time DateTime binding
        colDate.setCellValueFactory(new PropertyValueFactory<>("bookingDateTime"));

        // ✅ Format LocalDateTime in table
        colDate.setCellFactory(column -> new TableCell<Booking, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(formatter));
            }
        });

        loadBookings();
    }

    private void loadBookings() {
        bookingsTable.setItems(FXCollections.observableArrayList(
                bookingDAO.getByGuide(currentGuide.getId())));
    }

    // ===================== PROFILE TAB =====================

    private void setupProfileTab() {
        guideNameField.setText(currentGuide.getName());
        guidePhoneField.setText(currentGuide.getPhone());

        availabilityCombo.setItems(FXCollections.observableArrayList(
                "Available", "Busy", "On Leave"
        ));

        availabilityCombo.setValue(currentGuide.getAvailabilityStatus());
    }

    @FXML
    private void handleUpdateProfile(ActionEvent event) {

        currentGuide.setName(guideNameField.getText().trim());
        currentGuide.setPhone(guidePhoneField.getText().trim());
        currentGuide.setAvailabilityStatus(availabilityCombo.getValue());

        boolean ok = guideDAO.update(currentGuide);

        profileStatus.setText(
                ok ? "Profile updated successfully!" : "Update failed."
        );
    }

    // ===================== REVIEWS TAB =====================

    private void setupReviewsTable() {

        colReviewer.setCellValueFactory(new PropertyValueFactory<>("touristName"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
        colReviewDate.setCellValueFactory(new PropertyValueFactory<>("feedbackDate"));

        loadReviews();
    }

    private void loadReviews() {
        List<Feedback> reviews =
                feedbackDAO.getFeedbackForGuide(currentGuide.getId());

        reviewsTable.setItems(FXCollections.observableArrayList(reviews));
    }

    // ===================== LOGOUT =====================

    @FXML
    private void handleLogout(ActionEvent event) {

        Session.getInstance().clear();

        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/sahara/fxml/Login.fxml")
            );

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("SAHARA - Login");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}