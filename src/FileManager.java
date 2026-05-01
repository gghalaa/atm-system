import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileManager {
    private static final String FILE_PATH = "users.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private Set<String> loggedTransactions = new HashSet<>(); // Track logged transactions

    // Save a list of users to the file
    public void saveUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                writer.write(userToString(user));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    // Load users from the file
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                String name = parts[0].trim();
                String userID = parts[1].trim();
                String pin = parts[2].trim();
                User user = new User(name, userID, pin);

                for (int i = 3; i < parts.length; i += 3) {
                    if (i + 2 >= parts.length) {
                        System.out.println("Skipping incomplete account data for user: " + userID);
                        break;
                    }

                    String accountNumber = parts[i].trim();
                    String accountType = parts[i + 1].trim();
                    double balance = Double.parseDouble(parts[i + 2].trim());

                    Account account;
                    if (accountType.equalsIgnoreCase("Checking Account")) {
                        account = new CheckingAccount(accountNumber, balance, 100);
                    } else {
                        account = new SavingsAccount(accountNumber, balance, 1.5);
                    }
                    user.addAccount(account);
                }
                users.add(user);
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing balance: " + e.getMessage());
        }
        return users;
    }

    // Log a transaction to the transaction file
    public void logTransaction(Transaction transaction) {
        String transactionKey = transaction.getUserID() + transaction.getAccountNumber() + transaction.getType() + transaction.getAmount();
        if (!loggedTransactions.contains(transactionKey)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
                String logEntry = String.format("%s, %s, %s, %.2f, %s%n",
                        transaction.getUserID(),
                        transaction.getAccountNumber(),
                        transaction.getType(),
                        transaction.getAmount(),
                        transaction.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.write(logEntry);
                loggedTransactions.add(transactionKey); // Mark as logged
            } catch (IOException e) {
                System.err.println("Error logging transaction: " + e.getMessage());
            }
        }
    }

    // Get a user's transaction report
    public List<Transaction> getUserTransactionReport(String userID) {
        List<Transaction> userTransactions = new ArrayList<>();
        List<User> users = loadUsers();

        for (User user : users) {
            if (user.getUserID().equals(userID)) {
                userTransactions.addAll(user.getTransactionHistory());
                break;
            }
        }

        // Load transactions from the transaction file
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",\\s*");
                if (parts.length < 5) continue;

                String transactionUserID = parts[0].trim();
                String accountNumber = parts[1].trim();
                String type = parts[2].trim();
                double amount = Double.parseDouble(parts[3].trim());
                LocalDateTime timestamp = LocalDateTime.parse(parts[4].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                if (transactionUserID.equals(userID)) {
                    userTransactions.add(new Transaction(transactionUserID, accountNumber, type, amount, timestamp));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading transaction report: " + e.getMessage());
        }

        return userTransactions;
    }

    // Update the account balance
    public void updateAccountBalance(String userID, String accountNumber, double newBalance) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUserID().equals(userID)) {
                for (Account account : user.getAccounts()) {
                    if (account.getAccountNumber().equals(accountNumber)) {
                        account.setBalance(newBalance); // Ensure you have a setBalance method
                        break;
                    }
                }
                break; // Exit the outer loop after finding the user
            }
        }
        saveUsers(users); // Save changes back to file
    }

    // Update the user's password
    public void updateUserPassword(String userID, String newPassword) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUserID().equals(userID)) {
                user.setPin(newPassword); // Ensure you have a setPin method
                break;
            }
        }
        saveUsers(users); // Save changes back to file
    }

    // Convert a User object to a string for saving
    private String userToString(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getName()).append(",")
          .append(user.getUserID()).append(",")
          .append(user.getPin());

        for (Account account : user.getAccounts()) {
            sb.append(",").append(account.getAccountNumber())
              .append(",").append(account.getAccountType())
              .append(",").append(account.getBalance());
        }
        return sb.toString();
    }
}