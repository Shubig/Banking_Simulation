package com.bankingsim.service;

import com.bankingsim.dao.DbUtil;
import com.bankingsim.dao.TransactionDao;
import com.bankingsim.dao.AccountDao;
import com.bankingsim.model.Transaction;
import com.bankingsim.model.Account;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TransactionService {
    private final TransactionDao txDao = new TransactionDao();
    private final AccountDao accountDao = new AccountDao();
    private final EmailService emailService;

    public TransactionService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void deposit(int toAccount, BigDecimal amount) throws Exception {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount>0");
        // simple single-connection transaction
        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);
            Optional<Account> toOpt = accountDao.findByAccountNumber(toAccount);
            if (toOpt.isEmpty()) throw new SQLException("Account not found");
            Account to = toOpt.get();
            BigDecimal newBal = to.getBalance().add(amount);
            accountDao.updateBalance(toAccount, newBal);
            Transaction tx = new Transaction(UUID.randomUUID().toString(), null, toAccount, amount, "DEPOSIT");
            tx.setCreatedAt(LocalDateTime.now());
            txDao.insert(tx);
            conn.commit();

            // send email
            emailService.send(getUserEmail(to.getUserId()), "Deposit Alert", "₹" + amount + " deposited to account " + toAccount + ". New balance: ₹" + newBal);
        }
    }

    public void withdraw(int fromAccount, BigDecimal amount) throws Exception {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount>0");
        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);
            Optional<Account> fromOpt = accountDao.findByAccountNumber(fromAccount);
            if (fromOpt.isEmpty()) throw new SQLException("Account not found");
            Account from = fromOpt.get();
            if (from.getBalance().compareTo(amount) < 0) throw new SQLException("Insufficient funds");
            BigDecimal newBal = from.getBalance().subtract(amount);
            accountDao.updateBalance(fromAccount, newBal);
            Transaction tx = new Transaction(UUID.randomUUID().toString(), fromAccount, null, amount, "WITHDRAW");
            tx.setCreatedAt(LocalDateTime.now());
            txDao.insert(tx);
            conn.commit();

            emailService.send(getUserEmail(from.getUserId()), "Withdrawal Alert", "₹" + amount + " withdrawn from account " + fromAccount + ". New balance: ₹" + newBal);

            // low balance alert
            if (newBal.compareTo(from.getThreshold()) < 0) {
                emailService.send(getUserEmail(from.getUserId()), "Low Balance Alert", "Your account " + fromAccount + " is below threshold: ₹" + newBal);
            }
        }
    }

    public void transfer(int fromAccount, int toAccount, BigDecimal amount) throws Exception {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount>0");
        // do in single DB transaction with row locks (SELECT FOR UPDATE recommended in real DB)
        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);
            Optional<Account> fromOpt = accountDao.findByAccountNumber(fromAccount);
            Optional<Account> toOpt = accountDao.findByAccountNumber(toAccount);
            if (fromOpt.isEmpty() || toOpt.isEmpty()) throw new SQLException("Account(s) not found");
            Account from = fromOpt.get(), to = toOpt.get();
            if (from.getBalance().compareTo(amount) < 0) throw new SQLException("Insufficient funds");
            BigDecimal fromNew = from.getBalance().subtract(amount);
            BigDecimal toNew = to.getBalance().add(amount);
            accountDao.updateBalance(fromAccount, fromNew);
            accountDao.updateBalance(toAccount, toNew);
            Transaction tx = new Transaction(UUID.randomUUID().toString(), fromAccount, toAccount, amount, "TRANSFER");
            tx.setCreatedAt(LocalDateTime.now());
            txDao.insert(tx);
            conn.commit();

            // emails
            emailService.send(getUserEmail(from.getUserId()), "Transfer Sent", "₹" + amount + " transferred to account " + toAccount + ". New balance: ₹" + fromNew);
            emailService.send(getUserEmail(to.getUserId()), "Transfer Received", "₹" + amount + " received into account " + toAccount + ". New balance: ₹" + toNew);

            if (fromNew.compareTo(from.getThreshold()) < 0) {
                emailService.send(getUserEmail(from.getUserId()), "Low Balance Alert", "Your account " + fromAccount + " is below threshold: ₹" + fromNew);
            }
        }
    }

    public List<Transaction> history(int accountNumber) throws Exception {
        return txDao.findByAccount(accountNumber);
    }

    // simplistic lookup; implement UserDao method to fetch email by userId
    private String getUserEmail(int userId) {
        try {
            return new com.bankingsim.dao.UserDao().findById(userId).map(u -> u.getEmail()).orElse("no-reply@example.com");
        } catch (Exception e) { return "no-reply@example.com"; }
    }
}
