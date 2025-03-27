package ro.mpp2025.service;

import ro.mpp2025.domain.Trip;
import ro.mpp2025.repository.TripRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TripService {

    private final TripRepository tripRepo;

    public TripService(TripRepository tripRepo) {
        this.tripRepo = tripRepo;
    }

    public List<Trip> getAllTrips() {
        return (List<Trip>) tripRepo.findAll();
    }

    public List<Trip> searchTripsByObjectiveAndTime(String objective, LocalDate date, int startHour, int endHour) {
        return tripRepo.findTripsByObjectiveDateAndTimeRange(objective, date, startHour, endHour);
    }

    public Optional<Trip> getTripById(Long id) {
        return tripRepo.findOne(id);
    }

    public void updateAvailableSeats(Trip trip, int newAvailableSeats) {
        trip.setAvailableSeats(newAvailableSeats);
        tripRepo.update(trip);
    }
}
