package ro.mpp2025.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2025.domain.Trip;
import ro.mpp2025.utils.JdbcUtils;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class TripDBRepository implements TripRepository {
    private final JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(TripDBRepository.class);

    public TripDBRepository(Properties props) {
        logger.info("Initializing TripDatabaseRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Optional<Trip> findOne(Long id) {
        logger.traceEntry("Finding trip with ID {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM trips WHERE id = ?")) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String departureTimeStr = rs.getString("departureTime");
                LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                Trip trip = new Trip(
                        rs.getString("touristAttraction"),
                        rs.getString("transportCompany"),
                        departureTime,
                        rs.getDouble("price"),
                        rs.getInt("availableSeats")
                );
                trip.setId(rs.getLong("id"));
                return Optional.of(trip);
            }
        } catch (SQLException e) {
            logger.error("Error finding trip", e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Trip> findAll() {
        logger.traceEntry("Finding all trips");
        Connection con = dbUtils.getConnection();
        List<Trip> trips = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM trips"); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Trip trip = new Trip(
                        rs.getString("touristAttraction"),
                        rs.getString("transportCompany"),
                        rs.getTimestamp("departureTime").toLocalDateTime(),
                        rs.getDouble("price"),
                        rs.getInt("availableSeats")
                );
                trip.setId(rs.getLong("id"));
                trips.add(trip);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving trips", e);
        }
        return trips;
    }

    @Override
    public Optional<Trip> save(Trip trip) {
        logger.traceEntry("Saving trip {}", trip);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("INSERT INTO trips (touristAttraction, transportCompany, departureTime, price, availableSeats) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, trip.getTouristAttraction());
            stmt.setString(2, trip.getTransportCompany());
            String departureTimeStr = trip.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            stmt.setString(3, departureTimeStr); stmt.setDouble(4, trip.getPrice());
            stmt.setInt(5, trip.getAvailableSeats());
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                trip.setId(generatedKeys.getLong(1));
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error saving trip", e);
        }
        return Optional.of(trip);
    }

    @Override
    public Optional<Trip> delete(Long id) {
        logger.traceEntry("Deleting trip with id {}", id);
        Optional<Trip> trip = findOne(id);
        if (trip.isEmpty()) return Optional.empty();

        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM trips WHERE id = ?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return trip;
        } catch (SQLException e) {
            logger.error("Error deleting trip", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Trip> update(Trip trip) {
        logger.traceEntry("Updating trip {}", trip);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("UPDATE trips SET touristAttraction = ?, transportCompany = ?, departureTime = ?, price = ?, availableSeats = ? WHERE id = ?")) {
            stmt.setString(1, trip.getTouristAttraction());
            stmt.setString(2, trip.getTransportCompany());
            stmt.setTimestamp(3, Timestamp.valueOf(trip.getDepartureTime()));
            stmt.setDouble(4, trip.getPrice());
            stmt.setInt(5, trip.getAvailableSeats());
            stmt.setLong(6, trip.getId());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0 ? Optional.empty() : Optional.of(trip);
        } catch (SQLException e) {
            logger.error("Error updating trip", e);
        }
        return Optional.of(trip);
    }

    @Override
    public List<Trip> findTripsByObjectiveDateAndTimeRange(String objective, LocalDate departureDate, int startHour, int endHour) {
        logger.info("Finding trips for objective '{}' on '{}' between {} and {}", objective, departureDate, startHour, endHour);
        List<Trip> trips = new ArrayList<>();
        Connection con = dbUtils.getConnection();

        // Interogare corectată pentru SQLite
        String sql = "SELECT * FROM trips WHERE touristAttraction = ? " +
                "AND substr(departureTime, 1, 10) = ? " +  // Extrage doar data (YYYY-MM-DD)
                "AND CAST(substr(departureTime, 12, 2) AS INTEGER) BETWEEN ? AND ?";  // Extrage ora (HH)

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, objective);
            stmt.setString(2, departureDate.toString()); // Convertim LocalDate în String (YYYY-MM-DD)
            stmt.setInt(3, startHour);
            stmt.setInt(4, endHour);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String departureTimeStr = rs.getString("departureTime");
                LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                Trip trip = new Trip(
                        rs.getString("touristAttraction"),
                        rs.getString("transportCompany"),
                        departureTime,
                        rs.getDouble("price"),
                        rs.getInt("availableSeats")
                );
                trip.setId(rs.getLong("id"));
                trips.add(trip);
            }
        } catch (SQLException e) {
            logger.error("Error finding trips", e);
        }
        return trips;
    }


}
