package ro.mpp2025.service;

import ro.mpp2025.domain.Reservation;
import ro.mpp2025.domain.Trip;
import ro.mpp2025.repository.ReservationRepository;

public class ReservationService {

    private final ReservationRepository reservationRepo;

    public ReservationService(ReservationRepository reservationRepo) {
        this.reservationRepo = reservationRepo;
    }

    public void makeReservation(String clientName, String clientPhone, int ticketCount, Trip trip) {
        if (trip.getAvailableSeats() >= ticketCount) {
            Reservation reservation = new Reservation(clientName, clientPhone, ticketCount, trip);
            reservationRepo.save(reservation);
        } else {
            throw new IllegalArgumentException("Not enough available seats.");
        }
    }
}
