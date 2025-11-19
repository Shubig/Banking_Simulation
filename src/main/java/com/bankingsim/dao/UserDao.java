package com.bankingsim.dao;

import com.bankingsim.model.User;
import java.sql.*;
import java.util.Optional;

public class UserDao {

    // INSERT USER (REGISTER)
    public User insert(User u) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    u.setId(rs.getInt(1));
                    return u;
                }
            }
            throw new SQLException("Insert user failed: No ID returned.");
        }
    }

    // FIND USER BY USERNAME (LOGIN)
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, email, password_hash FROM users WHERE username=?";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password_hash")
                    ));
                }
                return Optional.empty();
            }
        }
    }

    // ⭐ NEW → FIND USER BY ID (for AlertService)
    public Optional<User> findById(int id) throws SQLException {
        String sql = "SELECT id, username, email, password_hash FROM users WHERE id=?";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password_hash")
                    ));
                }
                return Optional.empty();
            }
        }
    }


    public Optional<String> getEmail(int userId) throws SQLException {
        String sql = "SELECT email FROM users WHERE id=?";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String email = rs.getString("email");
                    return Optional.ofNullable(email); // safely wrap
                }
            }
        }

        return Optional.empty(); // NO MORE NULL RETURN
    }

}
