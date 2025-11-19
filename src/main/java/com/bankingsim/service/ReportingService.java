package com.bankingsim.service;

import com.bankingsim.dao.TransactionDao;
import com.bankingsim.model.Transaction;
import java.io.*;
import java.nio.file.*;
import java.util.List;

public class ReportingService {
    private final TransactionDao txDao = new TransactionDao();
    private final Path reportsDir = Paths.get("reports");

    public ReportingService() { try { Files.createDirectories(reportsDir); } catch (IOException ignored) {} }

    public Path exportAccountTransactions(int accountNumber, String fileName) throws Exception {
        List<Transaction> list = txDao.findByAccount(accountNumber);
        Path out = reportsDir.resolve(fileName);
        try (BufferedWriter w = Files.newBufferedWriter(out)) {
            w.write("Transactions for account " + accountNumber + "\n");
            for (Transaction t : list) w.write(String.format("%s | %s | %s | %s%n", t.getTransactionId(), t.getType(), t.getAmount(), t.getCreatedAt()));
        }
        return out;
    }
}
