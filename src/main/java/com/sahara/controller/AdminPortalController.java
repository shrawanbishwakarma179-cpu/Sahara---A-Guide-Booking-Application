package com.sahara.controller;

import com.sahara.dao.*;
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

import java.util.List;

/**
 * AdminPortalController — handles Admin portal:
 * - Dashboard (statistics)
 * - Manage Bookings (approve / reject)
 * - Manage Tourists
 * - Manage Guides
 * - Manage Tours
 */
public class AdminPortalController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label totalTouristsLabel;
    @FXML private Label totalGuidesLabel;

    // ---- Bookings Tab ----
    @FXML private TableView<Booking>             bookingsTable;
    @FXML private TableColumn<Booking, Integer>  colBId;
    @FXML private TableColumn<Booking, String>   colBTourist;
    @FXML private TableColumn<Booking, String>   colBGuide;
    @FXML private TableColumn<Booking, String>   colBDate;
    @FXML private TableColumn<Booking, String>   colBStatus;
    @FXML private TableColumn<Booking, String>   colBPayment;

    // ---- Tourists Tab ----
    @FXML private TableView<Tourist>             touristsTable;
    @FXML private TableColumn<Tourist, Integer>  colTId;
    @FXML private TableColumn<Tourist, String>   colTName;
    @FXML private TableColumn<Tourist, String>   colTEmail;
    @FXML private TableColumn<Tourist, String>   colTPhone;

    // ---- Guides Tab ----
    @FXML private TableView<Guide>               guidesTable;
    @FXML private TableColumn<Guide, Integer>    colGId;
    @FXML private TableColumn<Guide, String>     colGName;
    @FXML private TableColumn<Guide, String>     colGEmail;
    @FXML private TableColumn<Guide, String>     colGAvail;
    @FXML private TableColumn<Guide, Double>     colGRating;

    // ---- Tours Tab ----
    @FXML private TableView<Tour>                toursTable;
    @FXML private TableColumn<Tour, String>      colPId;
    @FXML private TableColumn<Tour, String>      colPName;
    @FXML private TableColumn<Tour, String>      colPDuration;
    @FXML private TableColumn<Tour, Double>      colPPrice;

    @FXML private TextField  newTourIdField;
    @FXML private TextField  newTourNameField;
    @FXML private TextField  newTourDurationField;
    @FXML private TextField  newTourPriceField;
    @FXML private Label      tourStatus;

    private final BookingDAO bookingDAO = new BookingDAO();
    private final TouristDAO touristDAO = new TouristDAO();
    private final GuideDAO   guideDAO   = new GuideDAO();
    private final TourDAO    tourDAO    = new TourDAO();

    private Admin currentAdmin;

    @FXML
    public void initialize() {
        currentAdmin = (Admin) Session.getInstance().getCurrentUser();
        welcomeLabel.setText("Welcome, " + currentAdmin.getName() + " (Admin)");

        loadDashboard();
        setupBookingsTable();
        setupTouristsTable();
        setupGuidesTable();
        setupToursTable();
    }

    // ===================== DASHBOARD =====================

    private void loadDashboard() {
        totalBookingsLabel.setText("Total Bookings: " + bookingDAO.getTotalBookings());
        totalTouristsLabel.setText("Total Tourists: " + touristDAO.getAllTourists().size());
        totalGuidesLabel.setText("Total Guides:   " + guideDAO.getAllGuides().size());
    }

    // ===================== BOOKINGS TAB =====================

    private void setupBookingsTable() {
        colBId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colBTourist.setCellValueFactory(new PropertyValueFactory<>("touristName"));
        colBGuide.setCellValueFactory(new PropertyValueFactory<>("guideName"));
        colBDate.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        colBStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colBPayment.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        loadAllBookings();
    }

    private void loadAllBookings() {
        bookingsTable.setItems(FXCollections.observableArrayList(bookingDAO.getAllBookings()));
    }

    @FXML
    private void handleApproveBooking(ActionEvent event) {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertUtil.showError("No Selection", "Please select a booking."); return; }
        boolean ok = bookingDAO.updateStatus(selected.getBookingId(), "Approved", currentAdmin.getId());
        if (ok) { AlertUtil.showInfo("Approved", "Booking #" + selected.getBookingId() + " approved."); loadAllBookings(); }
    }

    @FXML
    private void handleRejectBooking(ActionEvent event) {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertUtil.showError("No Selection", "Please select a booking."); return; }
        boolean ok = bookingDAO.updateStatus(selected.getBookingId(), "Rejected", currentAdmin.getId());
        if (ok) { AlertUtil.showInfo("Rejected", "Booking #" + selected.getBookingId() + " rejected."); loadAllBookings(); }
    }

    @FXML
    private void handleMarkPaid(ActionEvent event) {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertUtil.showError("No Selection", "Please select a booking."); return; }
        boolean ok = bookingDAO.updatePaymentStatus(selected.getBookingId(), "Paid");
        if (ok) { AlertUtil.showInfo("Paid", "Payment marked as Paid."); loadAllBookings(); }
    }

    // ===================== TOURISTS TAB =====================

    private void setupTouristsTable() {
        colTId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        touristsTable.setItems(FXCollections.observableArrayList(touristDAO.getAllTourists()));
    }

    @FXML
    private void handleDeleteTourist(ActionEvent event) {
        Tourist selected = touristsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertUtil.showError("No Selection", "Please select a tourist."); return; }
        if (AlertUtil.showConfirm("Delete", "Delete tourist: " + selected.getName() + "?")) {
            boolean ok = touristDAO.delete(selected.getId());
            if (ok) setupTouristsTable();
        }
    }

    // ===================== GUIDES TAB =====================

    private void setupGuidesTable() {
        colGId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colGName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colGEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colGAvail.setCellValueFactory(new PropertyValueFactory<>("availabilityStatus"));
        colGRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        guidesTable.setItems(FXCollections.observableArrayList(guideDAO.getAllGuides()));
    }

    @FXML
    private void handleDeleteGuide(ActionEvent event) {
        Guide selected = guidesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertUtil.showError("No Selection", "Please select a guide."); return; }
        if (AlertUtil.showConfirm("Delete", "Delete guide: " + selected.getName() + "?")) {
            boolean ok = guideDAO.delete(selected.getId());
            if (ok) setupGuidesTable();
        }
    }

    // ===================== TOURS TAB =====================

    private void setupToursTable() {
        colPId.setCellValueFactory(new PropertyValueFactory<>("tourId"));
        colPName.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        colPDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colPPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        toursTable.setItems(FXCollections.observableArrayList(tourDAO.getAllTours()));
    }

    @FXML
    private void handleAddTour(ActionEvent event) {
        String id       = newTourIdField.getText().trim();
        String name     = newTourNameField.getText().trim();
        String duration = newTourDurationField.getText().trim();
        String priceStr = newTourPriceField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || priceStr.isEmpty()) {
            tourStatus.setText("ID, Name and Price are required."); return;
        }
        double price;
        try { price = Double.parseDouble(priceStr); }
        catch (NumberFormatException e) { tourStatus.setText("Invalid price."); return; }

        Tour tour = new Tour(id, name, duration, price);
        boolean ok = tourDAO.addTour(tour);
        if (ok) {
            tourStatus.setText("Tour added successfully!");
            setupToursTable();
            newTourIdField.clear(); newTourNameField.clear();
            newTourDurationField.clear(); newTourPriceField.clear();
        } else {
            tourStatus.setText("Failed to add tour. ID may already exist.");
        }
    }

    @FXML
    private void handleDeleteTour(ActionEvent event) {
        Tour selected = toursTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertUtil.showError("No Selection", "Please select a tour."); return; }
        if (AlertUtil.showConfirm("Delete Tour", "Delete: " + selected.getTourName() + "?")) {
            boolean ok = tourDAO.deleteTour(selected.getTourId());
            if (ok) setupToursTable();
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
        } catch (Exception e) { e.printStackTrace(); }
    }
}
