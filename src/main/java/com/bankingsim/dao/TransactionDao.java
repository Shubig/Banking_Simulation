package com.bankingsim.dao;

import com.bankingsim.model.Transaction;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {
    public void insert(Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_id,from_account,to_account,amount,type) VALUES (?,?,?,?,?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getTransactionId());
            if (t.getFromAccount() == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, t.getFromAccount());
            if (t.getToAccount() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, t.getToAccount());
            ps.setBigDecimal(4, t.getAmount());
            ps.setString(5, t.getType());
            ps.executeUpdate();
        }
    }

    public List<Transaction> findByAccount(int accountNumber) throws SQLException {
        String sql = "SELECT transaction_id,from_account,to_account,amount,type,created_at FROM transactions WHERE from_account = ? OR to_account = ? ORDER BY created_at DESC";
        List<Transaction> out = new ArrayList<>();
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, accountNumber);
            ps.setInt(2, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction(rs.getString("transaction_id"),
                            rs.getInt("from_account")==0?null:rs.getInt("from_account"),
                            rs.getInt("to_account")==0?null:rs.getInt("to_account"),
                            rs.getBigDecimal("amount"),
                            rs.getString("type"));
                    t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    out.add(t);
                }
            }
        }
        return out;
    }
}
