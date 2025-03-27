package ro.mpp2025.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.mpp2025.repository.SoftUserDBRepository;
import ro.mpp2025.repository.TripDBRepository;
import ro.mpp2025.service.SoftUserService;
import ro.mpp2025.service.TripService;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final SoftUserService userService;
    private final TripService tripService;

    public LoginController() {
        Properties props = new Properties();
        try {
            props.load(new FileReader("src/bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config " + e);
        }
        this.userService = new SoftUserService(new SoftUserDBRepository(props));
        this.tripService = new TripService(new TripDBRepository(props));
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (userService.login(username, password).isPresent()) {
            openTripWindow();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password.");
            alert.showAndWait();
        }
    }

    private void openTripWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trips.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Trips Management");
            stage.setScene(new Scene(root));
            stage.show();

            ((Stage) usernameField.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to open the trips window.");
            alert.showAndWait();
        }
    }
}
