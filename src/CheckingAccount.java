import java.time.LocalDateTime;

public class CheckingAccount extends Account {
    private double overdraftLimit;

    public CheckingAccount(String accountNumber, double initialBalance, double overdraftLimit) {
        super(accountNumber, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    // Adjusted withdraw method to include User object for logging
    public boolean withdraw(double amount, User user) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }

        double availableBalance = balance + overdraftLimit;
        if (amount > availableBalance) {
            throw new IllegalArgumentException(String.format(
                "Withdrawal amount of %.2f exceeds available balance of %.2f (including overdraft limit of %.2f).",
                amount, balance, overdraftLimit));
        }

        balance -= amount;

        // Create a transaction and log it
        Transaction transaction = new Transaction(user.getUserID(), accountNumber, "Withdrawal", amount, LocalDateTime.now()); // Include timestamp
        user.addTransaction(transaction); // Add to user's transaction history

        // Assuming FileManager is accessible here to log the transaction
        FileManager fileManager = new FileManager();
        fileManager.logTransaction(transaction); // Log transaction to file

        return true; // Indicate that the withdrawal was successful
    }

    @Override
    public String getAccountType() {
        return "Checking Account";
    }

    public double getOverdraftLimit() {
        return overdraftLimit; // Getter for overdraft limit
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit; // Setter for overdraft limit
    }
}