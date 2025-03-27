package ro.mpp2025.repository;

import ro.mpp2025.domain.Trip;

import java.time.LocalDate;
import java.util.List;

public interface TripRepository extends Repository<Long, Trip> {
    List<Trip> findTripsByObjectiveDateAndTimeRange(String objective, LocalDate departureDate, int startHour, int endHour);
}
