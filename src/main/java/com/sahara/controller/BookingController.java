package com.sahara.controller;

import com.sahara.model.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class BookingController {

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Integer> colBId;
    @FXML private TableColumn<Booking, String> colTourist;
    @FXML private TableColumn<Booking, LocalDateTime> colDateTime;
    @FXML private TableColumn<Booking, String> colStatus;
    @FXML private TableColumn<Booking, String> colPayment;

    @FXML private DatePicker bookingDatePicker;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private TextField touristNameField;
    @FXML private TextField paymentStatusField;

    private final ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {

        // ---- Table Columns ----
        colBId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colTourist.setCellValueFactory(new PropertyValueFactory<>("touristName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        colDateTime.setCellValueFactory(new PropertyValueFactory<>("bookingDateTime"));

        // Format DateTime column
        colDateTime.setCellFactory(col -> new TableCell<Booking, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(formatter));
            }
        });

        // ---- Time Slots ----
        timeComboBox.setItems(FXCollections.observableArrayList(
                "09:00", "10:00", "11:00", "12:00",
                "13:00", "14:00", "15:00", "16:00"
        ));

        // ---- Disable Past Dates ----
        bookingDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });

        bookingsTable.setItems(bookingList);
    }

    @FXML
    private void handleBookButton() {

        String touristName = touristNameField.getText();
        LocalDate selectedDate = bookingDatePicker.getValue();
        String selectedTime = timeComboBox.getValue();
        String paymentStatus = paymentStatusField.getText();

        // ---- Validation ----
        if (touristName.isEmpty() || selectedDate == null
                || selectedTime == null || paymentStatus.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Error", "Fill all fields");
            return;
        }

        // Convert to LocalDateTime
        LocalTime time = LocalTime.parse(selectedTime);
        LocalDateTime bookingDateTime = LocalDateTime.of(selectedDate, time);

        // Current time
        LocalDateTime now = LocalDateTime.now();

        // ---- Real-time validation ----
        if (bookingDateTime.isBefore(now)) {
            showAlert(Alert.AlertType.ERROR,
                    "Invalid Booking",
                    "Cannot book past date/time");
            return;
        }

        // ---- Create Booking ----
        Booking booking = new Booking();
        booking.setBookingId(bookingList.size() + 1);
        booking.setTouristName(touristName);
        booking.setBookingDateTime(bookingDateTime);
        booking.setPaymentStatus(paymentStatus);
        booking.setStatus("Pending");

        bookingList.add(booking);

        // ---- Clear Fields ----
        touristNameField.clear();
        bookingDatePicker.setValue(null);
        timeComboBox.setValue(null);
        paymentStatusField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}