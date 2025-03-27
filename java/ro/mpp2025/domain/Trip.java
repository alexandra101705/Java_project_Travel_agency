package ro.mpp2025.domain;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Trip extends Entity<Long>{

    String touristAttraction;
    String transportCompany;
    LocalDateTime departureTime;
    Double price;
    Integer availableSeats;

    public Trip(String touristAttraction, String transportCompany, LocalDateTime departureTime, Double price, Integer availableSeats) {
        this.touristAttraction = touristAttraction;
        this.transportCompany = transportCompany;
        this.departureTime = departureTime;
        this.price = price;
        this.availableSeats = availableSeats;
    }

    public String getTouristAttraction() {
        return touristAttraction;
    }

    public String getTransportCompany() {
        return transportCompany;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setTouristAttraction(String touristAttraction) {
        this.touristAttraction = touristAttraction;
    }

    public void setTransportCompany(String transportCompany) {
        this.transportCompany = transportCompany;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    @Override
    public String toString() {return touristAttraction + " " + transportCompany + " " + departureTime + " " + price + " " + availableSeats + " " + getId();}
}
