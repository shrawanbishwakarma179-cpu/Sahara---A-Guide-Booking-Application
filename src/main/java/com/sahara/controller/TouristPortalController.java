package com.sahara.controller;

import com.sahara.dao.BookingDAO;
import com.sahara.dao.FeedbackDAO;
import com.sahara.dao.GuideDAO;
import com.sahara.dao.TourDAO;
import com.sahara.model.*;
import com.sahara.util.AlertUtil;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TouristPortalController — handles all Tourist portal tabs:
 * - Browse Tours
 * - Book a Tour
 * - My Bookings
 * - Give Feedback
 */
public class TouristPortalController {

    // ---- Header ----
    @FXML private Label welcomeLabel;

    // ---- Tours Tab ----
    @FXML private TableView<Tour> toursTable;
    @FXML private TableColumn<Tour, String> colTourId;
    @FXML private TableColumn<Tour, String> colTourName;
    @FXML private TableColumn<Tour, String> colDuration;
    @FXML private TableColumn<Tour, Double> colPrice;

    // ---- Book Tab ----
    @FXML private ComboBox<Guide>    guideCombo;
    @FXML private DatePicker         bookingDatePicker;
    @FXML private ComboBox<String>   paymentCombo;
    @FXML private ListView<Tour>     tourSelectList;
    @FXML private Label              bookingStatus;

    // ---- My Bookings Tab ----
    @FXML private TableView<Booking>             myBookingsTable;
    @FXML private TableColumn<Booking, Integer>  colBId;
    @FXML private TableColumn<Booking, String>   colBGuide;
    @FXML private TableColumn<Booking, String>   colBDate;
    @FXML private TableColumn<Booking, String>   colBStatus;
    @FXML private TableColumn<Booking, String>   colBPayment;

    // ---- Feedback Tab ----
    @FXML private ComboBox<Booking>  feedbackBookingCombo;
    @FXML private Spinner<Integer>   ratingSpinner;
    @FXML private TextArea           commentArea;
    @FXML private Label              feedbackStatus;

    private final TourDAO    tourDAO    = new TourDAO();
    private final GuideDAO   guideDAO   = new GuideDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    private Tourist currentTourist;

    @FXML
    public void initialize() {
        currentTourist = (Tourist) Session.getInstance().getCurrentUser();
        welcomeLabel.setText("Welcome, " + currentTourist.getName() + "!");

        setupToursTable();
        setupBookTab();
        setupMyBookingsTable();
        setupFeedbackTab();
    }

    // ===================== TOURS TAB =====================

    private void setupToursTable() {
        colTourId.setCellValueFactory(new PropertyValueFactory<>("tourId"));
        colTourName.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        toursTable.setItems(FXCollections.observableArrayList(tourDAO.getAllTours()));
    }

    // ===================== BOOK TAB =====================

    private void setupBookTab() {
        // Guides
        guideCombo.setItems(FXCollections.observableArrayList(guideDAO.getAvailableGuides()));

        // Payment options
        paymentCombo.setItems(FXCollections.observableArrayList("Cash", "Card", "Digital Wallet"));
        paymentCombo.setValue("Cash");

        // Tour multi-select
        tourSelectList.setItems(FXCollections.observableArrayList(tourDAO.getAllTours()));
        tourSelectList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Default date
        bookingDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleBookNow(ActionEvent event) {
        Guide selectedGuide = guideCombo.getValue();
        LocalDate date      = bookingDatePicker.getValue();
        String payment      = paymentCombo.getValue();
        List<Tour> selectedTours = tourSelectList.getSelectionModel().getSelectedItems();

        if (selectedGuide == null || date == null || selectedTours.isEmpty()) {
            bookingStatus.setText("Please select a guide, date and at least one tour.");
            return;
        }

        List<String> tourIds = new ArrayList<>();
        for (Tour t : selectedTours) tourIds.add(t.getTourId());

        Booking booking = new Booking(currentTourist.getId(), selectedGuide.getId(), date, payment);
        boolean ok = bookingDAO.createBooking(booking, tourIds);

        if (ok) {
            bookingStatus.setText("Booking submitted! Awaiting admin approval.");
            loadMyBookings();
        } else {
            bookingStatus.setText("Booking failed. Please try again.");
        }
    }

    // ===================== MY BOOKINGS TAB =====================

    private void setupMyBookingsTable() {
        colBId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colBGuide.setCellValueFactory(new PropertyValueFactory<>("guideName"));
        colBDate.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        colBStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colBPayment.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        loadMyBookings();
    }

    private void loadMyBookings() {
        myBookingsTable.setItems(FXCollections.observableArrayList(
            bookingDAO.getByTourist(currentTourist.getId())));
    }

    // ===================== FEEDBACK TAB =====================

    private void setupFeedbackTab() {
        List<Booking> completed = bookingDAO.getByTourist(currentTourist.getId());
        feedbackBookingCombo.setItems(FXCollections.observableArrayList(completed));

        SpinnerValueFactory<Integer> svf =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 5);
        ratingSpinner.setValueFactory(svf);
    }

    @FXML
    private void handleSubmitFeedback(ActionEvent event) {
        Booking booking = feedbackBookingCombo.getValue();
        if (booking == null) {
            feedbackStatus.setText("Please select a booking.");
            return;
        }
        String comment = commentArea.getText().trim();
        int rating     = ratingSpinner.getValue();

        Feedback fb = new Feedback();
        fb.setTouristId(currentTourist.getId());
        fb.setGuideId(booking.getGuideId());
        fb.setBookingId(booking.getBookingId());
        fb.setRating(rating);
        fb.setComment(comment);
        fb.setFeedbackDate(LocalDate.now());

        boolean ok = feedbackDAO.submitFeedback(fb);
        if (ok) {
            // Update guide rating
            double avg = feedbackDAO.getAverageRating(booking.getGuideId());
            new GuideDAO().updateRating(booking.getGuideId(), avg);
            feedbackStatus.setText("Feedback submitted! Thank you.");
            commentArea.clear();
        } else {
            feedbackStatus.setText("Failed to submit feedback.");
        }
    }

    // ===================== LOGOUT =====================

    @FXML
    private void handleLogout(ActionEvent event) {
        Session.getInstance().clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/sahara/fxml/Login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("SAHARA - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
