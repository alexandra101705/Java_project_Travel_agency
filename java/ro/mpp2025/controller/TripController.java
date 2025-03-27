package ro.mpp2025.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ro.mpp2025.domain.Trip;
import ro.mpp2025.repository.ReservationDBRepository;
import ro.mpp2025.repository.SoftUserDBRepository;
import ro.mpp2025.repository.TripDBRepository;
import ro.mpp2025.service.ReservationService;
import ro.mpp2025.service.SoftUserService;
import ro.mpp2025.service.TripService;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

public class TripController {

    @FXML
    private TableView<Trip> tripsTable;

    @FXML
    private TableColumn<Trip, String> touristAttractionColumn;

    @FXML
    private TableColumn<Trip, String> transportCompanyColumn;

    @FXML
    private TableColumn<Trip, String> departureTimeColumn;

    @FXML
    private TableColumn<Trip, Double> priceColumn;

    @FXML
    private TableColumn<Trip, Integer> availableSeatsColumn;

    @FXML
    private TextField txtTouristAttraction;

    @FXML
    private TextField txtStartHour;

    @FXML
    private TextField txtEndHour;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField clientNameField;

    @FXML
    private TextField clientPhoneField;

    @FXML
    private TextField ticketCountField;

    private final TripService tripService;
    private final ReservationService reservationService;

    public TripController() {
        Properties props = new Properties();
        try {
            props.load(new FileReader("src/bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config " + e);
        }
        this.tripService = new TripService(new TripDBRepository(props));
        TripDBRepository tripRepo = new TripDBRepository(props);
        this.reservationService = new ReservationService(new ReservationDBRepository(props, tripRepo));

    }

    @FXML
    public void initialize() {
        touristAttractionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTouristAttraction()));
        transportCompanyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransportCompany()));
        departureTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartureTime().toString()));
        priceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrice()));
        availableSeatsColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getAvailableSeats()));

        tripsTable.getItems().setAll(tripService.getAllTrips());
    }

    @FXML
    private void searchTrips() {
        String objective = txtTouristAttraction.getText();
        int startHour = Integer.parseInt(txtStartHour.getText());
        int endHour = Integer.parseInt(txtEndHour.getText());
        LocalDate date = datePicker.getValue();

        if (date == null) {
            showError("Please select a date.");
            return;
        }

        List<Trip> trips = tripService.searchTripsByObjectiveAndTime(objective, date, startHour, endHour);

        tripsTable.getItems().setAll(trips);
    }

    @FXML
    private void makeReservation() {
        Trip selectedTrip = tripsTable.getSelectionModel().getSelectedItem();

        if (selectedTrip == null) {
            showError("Please select a trip to reserve.");
            return;
        }

        String clientName = clientNameField.getText();
        String clientPhone = clientPhoneField.getText();
        int ticketCount;

        try {
            ticketCount = Integer.parseInt(ticketCountField.getText());
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for tickets.");
            return;
        }

        try {
            reservationService.makeReservation(clientName, clientPhone, ticketCount, selectedTrip);
            tripsTable.getItems().setAll(tripService.getAllTrips());
            showInfo("Reservation successful!");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void logout() {
        Stage stage = (Stage) tripsTable.getScene().getWindow();
        stage.close();
        openLoginScreen();
    }

    private void openLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
