package ro.mpp2025;

import ro.mpp2025.domain.Reservation;
import ro.mpp2025.domain.SoftUser;
import ro.mpp2025.domain.Trip;
import ro.mpp2025.repository.*;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {

        Properties props = new Properties();
        try {
            props.load(new FileReader("src/bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config " + e);
        }

        TripDBRepository tripRepo = new TripDBRepository(props);
        ReservationDBRepository reservationRepo = new ReservationDBRepository(props, tripRepo);
        SoftUserRepository softUserRepo = new SoftUserDBRepository(props);

        launch(args);

//        System.out.println("=== TRIPS ===");
//        for (Trip trip : tripRepo.findAll()) {
//            System.out.println(trip);
//        }
//
//
//        System.out.println("\n=== RESERVATIONS ===");
//        for (Reservation reservation : reservationRepo.findAll()) {
//            System.out.println(reservation);
//        }
//
//
//        System.out.println("\n=== USERS ===");
//        for (SoftUser user : softUserRepo.findAll()) {
//            System.out.println(user);
//        }
//
//        System.out.println("=== TRIPS FILTER ===");
//        LocalDate searchDate = LocalDate.of(2025, 3, 18);
//        List<Trip> trips = tripRepo.findTripsByObjectiveDateAndTimeRange("Castelul Banffy", searchDate,10, 12);
//        for (Trip trip : trips) {
//            System.out.println(trip);
//        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Tourism Reservation System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}