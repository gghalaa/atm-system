import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    protected String accountNumber;
    protected double balance;
    protected List<Transaction> transactionHistory;

    public Account(String accountNumber, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    // Deposit money into the account
    public boolean deposit(double amount, User user, FileManager fileManager) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }

        balance += amount; // Update the balance
        logTransaction("Deposit", amount, user); // Log the transaction

        // Log the transaction without checking for duplicates
        fileManager.logTransaction(new Transaction(user.getUserID(), accountNumber, "Deposit", amount));

        // Update the balance in the file
        fileManager.updateAccountBalance(user.getUserID(), accountNumber, balance);

        return true; // Indicate success
    }

    // Withdraw money from the account
    public boolean withdraw(double amount, User user, FileManager fileManager) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal.");
        }

        balance -= amount; // Update the balance
        logTransaction("Withdrawal", amount, user); // Log the transaction

        // Log the transaction without checking for duplicates
        fileManager.logTransaction(new Transaction(user.getUserID(), accountNumber, "Withdrawal", amount));

        // Update the balance in the file
        fileManager.updateAccountBalance(user.getUserID(), accountNumber, balance);

        return true; // Indicate success
    }

    // Log a transaction to history only
    protected void logTransaction(String type, double amount, User user) {
        Transaction transaction = new Transaction(user.getUserID(), accountNumber, type, amount);
        transactionHistory.add(transaction);
    }

    // Set balance method
    public void setBalance(double newBalance) {
        if (newBalance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }
        this.balance = newBalance; // Update the balance
    }

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    // Abstract method for subclasses to define account type
    public abstract String getAccountType();
}