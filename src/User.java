import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String userID;
    private String pin; // Store as plain text
    private List<Account> accounts;
    private boolean isAccountLocked;
    private List<Transaction> transactionHistory;

    public User(String name, String userID, String pin) {
        this.name = name;
        this.userID = userID;
        this.pin = pin; // Store PIN as plain text
        this.accounts = new ArrayList<>();
        this.isAccountLocked = false; // Default account status is unlocked
        this.transactionHistory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getUserID() {
        return userID;
    }

    public String getPin() {
        return pin; // Return the PIN as plain text
    }

    public void setPin(String pin) {
        this.pin = pin; // Update PIN in plain text
    }

    public List<Account> getAccounts() {
        return accounts; // Returns the list of accounts associated with the user
    }

    public boolean isAccountLocked() {
        return isAccountLocked; // Returns the account lock status
    }

    public void setAccountLocked(boolean accountLocked) {
        isAccountLocked = accountLocked; // Method to lock or unlock the account
    }

    public void addAccount(Account account) {
        accounts.add(account); // Adds a new account to the user's account list
    }

    public boolean hasAccounts() {
        return !accounts.isEmpty(); // Check if the user has any accounts
    }

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction); // Add transaction to history
        // Remove file logging from here to avoid duplicates
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory; // Returns transaction history
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(",").append(userID).append(",").append(pin); // Include PIN as plain text
        for (Account account : accounts) {
            sb.append(",").append(account.getAccountNumber())
              .append(",").append(account.getAccountType())
              .append(",").append(account.getBalance());
        }
        return sb.toString();
    }
}