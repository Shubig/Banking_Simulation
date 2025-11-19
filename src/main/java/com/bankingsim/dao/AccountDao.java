package com.bankingsim.dao;

import com.bankingsim.model.Account;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDao {

    // Insert new account
    public Account insert(Account a) throws SQLException {
        String sql = "INSERT INTO accounts (user_id, account_type, balance, threshold) VALUES (?,?,?,?)";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, a.getUserId());
            ps.setString(2, a.getAccountType());
            ps.setBigDecimal(3, a.getBalance());
            ps.setBigDecimal(4, a.getThreshold());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    a.setAccountNumber(rs.getInt(1));
                    return a;
                }
                throw new SQLException("Insert account failed");
            }
        }
    }

    // Find by account number
    public Optional<Account> findByAccountNumber(int accNo) throws SQLException {
        String sql = "SELECT account_number,user_id,account_type,balance,threshold,created_at FROM accounts WHERE account_number=?";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, accNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        }
    }

    // Find all accounts of a user
    public List<Account> findByUserId(int userId) throws SQLException {
        String sql = "SELECT account_number,user_id,account_type,balance,threshold,created_at FROM accounts WHERE user_id=?";

        List<Account> list = new ArrayList<>();

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }


    public List<Account> findAll() throws SQLException {
        String sql = "SELECT account_number,user_id,account_type,balance,threshold,created_at FROM accounts";

        List<Account> list = new ArrayList<>();

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Update balance
    public void updateBalance(int accNo, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance=? WHERE account_number=?";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, accNo);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Account not found: " + accNo);
            }
        }
    }

    // Update threshold
    public void updateThreshold(int accNo, BigDecimal threshold) throws SQLException {
        String sql = "UPDATE accounts SET threshold=? WHERE account_number=?";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBigDecimal(1, threshold);
            ps.setInt(2, accNo);
            ps.executeUpdate();
        }
    }

    // Helper to map result set to Account object
    private Account mapRow(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setAccountNumber(rs.getInt("account_number"));
        a.setUserId(rs.getInt("user_id"));
        a.setAccountType(rs.getString("account_type"));
        a.setBalance(rs.getBigDecimal("balance"));
        a.setThreshold(rs.getBigDecimal("threshold"));
        return a;
    }
}
