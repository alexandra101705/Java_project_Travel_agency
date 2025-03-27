package ro.mpp2025.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2025.domain.Reservation;
import ro.mpp2025.domain.Trip;
import ro.mpp2025.utils.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ReservationDBRepository implements ReservationRepository {
    private final JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(ReservationDBRepository.class);
    private final Repository<Long, Trip> tripRepository;

    public ReservationDBRepository(Properties props, Repository<Long, Trip> tripRepository) {
        logger.info("Initializing ReservationDatabaseRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
        this.tripRepository = tripRepository;
    }

    @Override
    public Optional<Reservation> findOne(Long id) {
        logger.traceEntry("Finding reservation with ID {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM reservations WHERE id = ?")) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getString("clientName"),
                        rs.getString("clientPhone"),
                        rs.getInt("ticketCount"),
                        null  // Trebuie să adăugăm un mecanism pentru a prelua și obiectul Trip
                );
                reservation.setId(rs.getLong("id"));
                return Optional.of(reservation);
            }
        } catch (SQLException e) {
            logger.error("Error finding reservation", e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Reservation> findAll() {
        logger.traceEntry("Finding all reservations");
        Connection con = dbUtils.getConnection();
        List<Reservation> reservations = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM reservations"); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Long tripId = rs.getLong("trip");
                Trip trip = tripRepository.findOne(tripId).orElse(null);
                Reservation reservation = new Reservation(
                        rs.getString("clientName"),
                        rs.getString("clientPhone"),
                        rs.getInt("ticketCount"),
                        trip
                );
                reservation.setId(rs.getLong("id"));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving reservations", e);
        }
        return reservations;
    }

    @Override
    public Optional<Reservation> save(Reservation reservation) {
        logger.traceEntry("Saving reservation {}", reservation);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("INSERT INTO reservations (clientName, clientPhone, ticketCount, trip ) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, reservation.getClientName());
            stmt.setString(2, reservation.getClientPhone());
            stmt.setInt(3, reservation.getTicketCount());
            stmt.setInt(4, Math.toIntExact(reservation.getTrip().getId()));
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            try (PreparedStatement updateStmt = con.prepareStatement("UPDATE trips SET availableSeats = availableSeats - ? WHERE id = ?")) {
                updateStmt.setInt(1, reservation.getTicketCount());
                updateStmt.setLong(2, reservation.getTrip().getId());
                updateStmt.executeUpdate();
            }
            if (generatedKeys.next()) {
                reservation.setId(generatedKeys.getLong(1));
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.error("Error saving reservation", e);
        }
        return Optional.of(reservation);
    }

    @Override
    public Optional<Reservation> delete(Long id) {
        logger.traceEntry("Deleting reservation with id {}", id);
        Optional<Reservation> reservation = findOne(id);
        if (reservation.isEmpty()) return Optional.empty();

        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM reservations WHERE id = ?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return reservation;
        } catch (SQLException e) {
            logger.error("Error deleting reservation", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Reservation> update(Reservation reservation) {
        logger.traceEntry("Updating reservation {}", reservation);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("UPDATE reservations SET clientName = ?, clientPhone = ?, ticketCount = ? WHERE id = ?")) {
            stmt.setString(1, reservation.getClientName());
            stmt.setString(2, reservation.getClientPhone());
            stmt.setInt(3, reservation.getTicketCount());
            stmt.setLong(4, reservation.getId());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0 ? Optional.empty() : Optional.of(reservation);
        } catch (SQLException e) {
            logger.error("Error updating reservation", e);
        }
        return Optional.of(reservation);
    }
}
