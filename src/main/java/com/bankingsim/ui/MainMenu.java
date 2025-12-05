package com.bankingsim.ui;

import com.bankingsim.model.User;
import com.bankingsim.model.Account;
import com.bankingsim.service.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class MainMenu {
    private static final Scanner sc = new Scanner(System.in);
    private static final AuthService auth = new AuthService();
    private static final EmailService emailService = new EmailService(System.getenv("SMTP_USER"), System.getenv("SMTP_PASS"));
    private static final TransactionService txService = new TransactionService(emailService);
    private static final AccountService accountService = new AccountService();
    private static final ReportingService reporting = new ReportingService();
    private static final AlertService alertService = new AlertService(emailService);

    public static void main(String[] args) throws Exception {
        // start alert monitoring every 60s (demo: 30s)
        alertService.start(30, 60);

        while (true) {
            System.out.println("\n=====================================");
            System.out.println("         🏦 BANKING SIMULATOR 🏦       ");
            System.out.println("  =====================================");
            System.out.println("1️⃣  Register 📝");
            System.out.println("2️⃣  Login 🔑");
            System.out.println("3️⃣  Exit 🚪");
            System.out.print("👉 Your Choice: ");

            int ch = Integer.parseInt(sc.nextLine().trim());
            if (ch == 1) doRegister();
            else if (ch == 2) doLogin();
            else if (ch == 3) { System.out.println("Bye"); alertService.stop(); break; }
            else System.out.println("Invalid choice");
        }
    }

    private static void doRegister() {
        try {
            System.out.print("Username: "); String u = sc.nextLine().trim();
            System.out.print("Email: "); String e = sc.nextLine().trim();
            System.out.print("Password: "); String p = sc.nextLine().trim();
            User user = auth.register(u, e, p);
            System.out.println("Registered: " + user.getUsername() + " (id=" + user.getId() + ")");
        } catch (Exception ex) { System.out.println("Register failed: " + ex.getMessage()); }
    }

    private static void doLogin() {
        try {
            System.out.print("Username: "); String u = sc.nextLine().trim();
            System.out.print("Password: "); String p = sc.nextLine().trim();
            User user = auth.login(u, p);
            System.out.println("Welcome " + user.getUsername());
            userMenu(user);
        } catch (Exception e) { System.out.println("Login failed: " + e.getMessage()); }
    }

    private static void userMenu(User user) {
        while (true) {
            System.out.println("\n=====================================");
            System.out.println("               USER MENU             ");
            System.out.println("=====================================");
            System.out.println("1️⃣  Create Account");
            System.out.println("2️⃣  💰 Deposit");
            System.out.println("3️⃣  🏧 Withdraw");
            System.out.println("4️⃣  🔄 Transfer Funds");
            System.out.println("5️⃣  📜 Transaction History");
            System.out.println("6️⃣  📁 Export Transactions to File");
            System.out.println("7️⃣  ⚙️ Update Threshold");
            System.out.println("8️⃣  💳 Display Balance");
            System.out.println("9️⃣  🚪 Logout");
            System.out.println("=====================================");
            System.out.println(" ");
            System.out.print("👉 Please choose an option: ");

            int c = Integer.parseInt(sc.nextLine().trim());
            System.out.println(" ");

            try {
                switch (c) {
                    case 1 -> {
                        System.out.print("Account type (Savings/Current): ");
                        String t = sc.nextLine().trim();
                        System.out.print("Initial deposit: ");
                        BigDecimal amt = new BigDecimal(sc.nextLine().trim());
                        Account acc = accountService.createAccount(user.getId(), t, amt);
                        System.out.println("Account created: " + acc.getAccountNumber());
                    }

                    case 2 -> {
                        System.out.print("Account no: ");
                        int a = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Amount: ");
                        BigDecimal am = new BigDecimal(sc.nextLine().trim());
                        txService.deposit(a, am);
                        System.out.println("\n┌───────────────────────────┐");
                        System.out.println("  │ 💰 Deposit Successful! 🎉│");
                        System.out.println("  └───────────────────────────┘");

                    }

                    case 3 -> {
                        System.out.print("Account no: ");
                        int a = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Amount: ");
                        BigDecimal am = new BigDecimal(sc.nextLine().trim());
                        txService.withdraw(a, am);
                        System.out.println("\n┌─────────────────────────────────┐");
                        System.out.println("  │ 🏧 Withdrawal Completed! 💸     │");
                        System.out.println("  └─────────────────────────────────┘");

                    }

                    case 4 -> {
                        System.out.print("From acc: ");
                        int fa = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("To acc: ");
                        int ta = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Amount: ");
                        BigDecimal am = new BigDecimal(sc.nextLine().trim());
                        txService.transfer(fa, ta, am);
                        System.out.println("\n┌──────────────────────────────────┐");
                        System.out.println("  │ 🔁 Transfer Completed! 💸        │");
                        System.out.println("  └──────────────────────────────────┘");

                    }

                    case 5 -> {
                        System.out.print("Account no: ");
                        int a = Integer.parseInt(sc.nextLine().trim());
                        var list = txService.history(a);

                        System.out.println("\n📜✨ Your Transaction History ✨📜");


                        list.forEach(t ->
                                System.out.println(t.getTransactionId() + " | "
                                        + t.getType() + " | "
                                        + t.getAmount() + " | "
                                        + t.getCreatedAt())
                        );
                        System.out.println("--------------------------------------------------");

                    }

                    case 6 -> {
                        System.out.print("Account no: ");
                        int a = Integer.parseInt(sc.nextLine().trim());

                        System.out.print("Enter your email to receive transactions: ");
                        String userEmail = sc.nextLine().trim();

                        System.out.print("File name (ex: txs-123.txt): ");
                        String fn = sc.nextLine().trim();

// Export file
                        String filePath = String.valueOf(reporting.exportAccountTransactions(a, fn));
                        java.io.File file = new java.io.File(filePath);

                        System.out.println("📁 File Exported: " + filePath);

// Email the TXT file
                        emailService.sendWithAttachment(
                                userEmail,
                                "📜 Transaction History | Account " + a,
                                "Dear Customer,\n\nYour transaction history is attached.\n\nThank you for banking with us!\n— BankingSim",
                                file
                        );

                        System.out.println("📧 Transaction history emailed successfully!");

                    }

                    case 7 -> {
                        System.out.print("Account no: ");
                        int a = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("New threshold: ");
                        BigDecimal th = new BigDecimal(sc.nextLine().trim());
                        accountService.updateThreshold(a, th);
                        System.out.println(
                                """
                                        -----------------------------------
                                           ✔ Threshold Successfully Updated! 🎯
                                        -----------------------------------"""
                                );

                    }


                    case 8 -> {
                        System.out.print("Account no: ");
                        int a = Integer.parseInt(sc.nextLine().trim());
                        BigDecimal balance = accountService.getBalance(a);
                        System.out.println(
                                "\n┌─────────────────────────────┐" +
                                "\n│     💰 CURRENT BALANCE      │" +
                                "\n├─────────────────────────────┤" +
                                "\n│        ₹" + balance + "     │" +
                                "\n└─────────────────────────────┘"
                                );

                    }

                    case 9 -> {
                        System.out.println("Logging out");
                        return;
                    }

                    default -> System.out.println("Invalid option");
                }

            } catch (Exception ex) {
                System.out.println("Operation failed: " + ex.getMessage());
            }
        }
    }}

