import java.time.LocalDateTime;

public class Transaction {
    private String userID;
    private String accountNumber;
    private String type; // "Deposit", "Withdrawal", "Interest", etc.
    private double amount;
    private LocalDateTime timestamp;

    // Constructor with timestamp
    public Transaction(String userID, String accountNumber, String type, double amount, LocalDateTime timestamp) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative.");
        }
        this.userID = userID;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // Constructor that sets the timestamp to now
    public Transaction(String userID, String accountNumber, String type, double amount) {
        this(userID, accountNumber, type, amount, LocalDateTime.now());
    }

    // Getters
    public String getUserID() {
        return userID;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Override toString for better representation
    @Override
    public String toString() {
        return String.format("User ID: %s | Account: %s | Type: %s | Amount: %.2f | Date: %s",
                userID, accountNumber, type, amount, timestamp);
    }
}