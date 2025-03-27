package ro.mpp2025.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2025.domain.SoftUser;
import ro.mpp2025.utils.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class SoftUserDBRepository implements SoftUserRepository {
    private final JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(SoftUserDBRepository.class);

    public SoftUserDBRepository(Properties props) {
        logger.info("Initializing SoftUserDatabaseRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Optional<SoftUser> findOne(Long id) {
        logger.traceEntry("Finding user with ID {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM soft_users WHERE id = ?")) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                SoftUser user = new SoftUser(rs.getString("username"), rs.getString("password"));
                user.setId(rs.getLong("id"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("Error finding user", e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<SoftUser> findAll() {
        logger.traceEntry("Finding all users");
        Connection con = dbUtils.getConnection();
        List<SoftUser> users = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM soft_users"); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                SoftUser user = new SoftUser(rs.getString("username"), rs.getString("password"));
                user.setId(rs.getLong("id"));
                users.add(user);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving users", e);
        }
        return users;
    }

    @Override
    public Optional<SoftUser> save(SoftUser user) {
        logger.traceEntry("Saving user {}", user);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("INSERT INTO soft_users (username, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error saving user", e);
        }
        return Optional.of(user);
    }

    @Override
    public Optional<SoftUser> delete(Long id) {
        logger.traceEntry("Deleting user with id {}", id);
        Optional<SoftUser> user = findOne(id);
        if (user.isEmpty()) return Optional.empty();

        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM soft_users WHERE id = ?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return user;
        } catch (SQLException e) {
            logger.error("Error deleting user", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<SoftUser> update(SoftUser user) {
        logger.traceEntry("Updating user {}", user);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("UPDATE soft_users SET username = ?, password = ? WHERE id = ?")) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setLong(3, user.getId());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0 ? Optional.empty() : Optional.of(user);
        } catch (SQLException e) {
            logger.error("Error updating user", e);
        }
        return Optional.of(user);
    }
}
