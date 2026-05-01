import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class ATM {
    private User currentUser;
    private List<User> users;
    private FileManager fileManager;

    public ATM() {
        users = new ArrayList<>();
        fileManager = new FileManager();
        users = fileManager.loadUsers();
    }

    public void start() {
        while (true) {
            String[] options = {"Login", "Register", "Exit"};
            int choice = JOptionPane.showOptionDialog(null, "Welcome to the ATM System", "ATM",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                options, options[0]);
            if (choice == 0) {
                login();
            } else if (choice == 1) {
                registerUser();
            } else if (choice == 2) {
                break; // Exit the program
            }
        }
    }

    private void login() {
        JTextField userIDField = new JTextField(20);
        JPasswordField pinField = new JPasswordField(20);
        Object[] message = {
            "User ID:", userIDField,
            "PIN:", pinField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String userID = userIDField.getText();
            String pin = new String(pinField.getPassword());
            for (User user : users) {
                if (user.getUserID().equals(userID) && user.getPin().equals(pin)) {
                    currentUser = user;
                    showFunctionalityPage();
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "Invalid User ID or PIN.");
        }
    }

    private void registerUser() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField nameField = new JTextField(20);
        JTextField userIDField = new JTextField(20);
        JPasswordField pinField = new JPasswordField(20);
        String[] accountTypes = {"Checking Account", "Savings Account"};
        JComboBox<String> accountTypeComboBox = new JComboBox<>(accountTypes);
        JTextField initialBalanceField = new JTextField(20);

        panel.add(new JLabel("Enter your name:"));
        panel.add(nameField);
        panel.add(new JLabel("Enter a new User ID:"));
        panel.add(userIDField);
        panel.add(new JLabel("Enter your PIN:"));
        panel.add(pinField);
        panel.add(new JLabel("Select Account Type:"));
        panel.add(accountTypeComboBox);
        panel.add(new JLabel("Enter Initial Balance:"));
        panel.add(initialBalanceField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Register", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String userID = userIDField.getText();
            String pin = new String(pinField.getPassword());
            String accountType = (String) accountTypeComboBox.getSelectedItem();
            String initialBalanceString = initialBalanceField.getText();

            if (userID.isEmpty() || pin.isEmpty() || name.isEmpty() || initialBalanceString.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled.");
                return;
            }

            double initialBalance;
            try {
                initialBalance = Double.parseDouble(initialBalanceString);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Initial balance must be a valid number.");
                return;
            }

            for (User user : users) {
                if (user.getUserID().equals(userID)) {
                    JOptionPane.showMessageDialog(null, "User ID already exists. Please choose another.");
                    return;
                }
            }

            Account newAccount;
            String accountNumber = "ACC" + System.currentTimeMillis(); // Unique account number based on timestamp
            if (accountType.equals("Checking Account")) {
                newAccount = new CheckingAccount(accountNumber, initialBalance, 100);
            } else {
                newAccount = new SavingsAccount(accountNumber, initialBalance, 1.5);
            }

            User newUser = new User(name, userID, pin);
            newUser.addAccount(newAccount);
            users.add(newUser);
            fileManager.saveUsers(users);
            JOptionPane.showMessageDialog(null, "Registration successful! You can now log in.");
        }
    }

    private void showFunctionalityPage() {
        if (currentUser == null || !currentUser.hasAccounts()) {
            JOptionPane.showMessageDialog(null, "No accounts available.");
            return;
        }

        // Account selection
        String[] accountOptions = currentUser.getAccounts().stream()
                .map(Account::getAccountNumber)
                .toArray(String[]::new);
        String selectedAccountNumber = (String) JOptionPane.showInputDialog(null, 
                "Select an account", "ATM System", 
                JOptionPane.PLAIN_MESSAGE, null, 
                accountOptions, accountOptions[0]);

        if (selectedAccountNumber == null) return; // User canceled selection

        Account selectedAccount = currentUser.getAccounts().stream()
                .filter(account -> account.getAccountNumber().equals(selectedAccountNumber))
                .findFirst()
                .orElse(null);

        if (selectedAccount == null) {
            JOptionPane.showMessageDialog(null, "Selected account not found.");
            return;
        }

        String accountInfo = "Current Account:\n" +
                             "Account Number: " + selectedAccount.getAccountNumber() + "\n" +
                             "Account Type: " + selectedAccount.getAccountType() + "\n" +
                             "Balance: " + selectedAccount.getBalance();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel infoLabel = new JLabel("<html>" + accountInfo.replace("\n", "<br>") + "</html>");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(e -> deposit(selectedAccount));
        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(e -> withdraw(selectedAccount));
        JButton transferButton = new JButton("Transfer");
        transferButton.addActionListener(e -> transfer(selectedAccount));
        JButton logoutButton = new JButton("Log Out");
        
        logoutButton.addActionListener(e -> logout());

        panel.add(infoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Space between info and buttons
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(transferButton);
        panel.add(logoutButton);

        JOptionPane.showMessageDialog(null, panel, "ATM System", JOptionPane.PLAIN_MESSAGE);
    }

    private void deposit(Account account) {
        String input = JOptionPane.showInputDialog(null, "Enter amount to deposit into account " + account.getAccountNumber() + ":");
        if (input == null || input.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Deposit amount cannot be empty.");
            return;
        }
        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(null, "Deposit amount must be greater than zero.");
                return;
            }
            account.deposit(amount, currentUser, fileManager); // Updated call
            JOptionPane.showMessageDialog(null, "Deposit successful! New balance: " + account.getBalance());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid amount entered. Please enter a numeric value.");
        }
    }
    
    private void withdraw(Account account) {
        String input = JOptionPane.showInputDialog(null, "Enter amount to withdraw from account " + account.getAccountNumber() + ":");
        if (input == null || input.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Withdrawal amount cannot be empty.");
            return;
        }
        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(null, "Withdrawal amount must be greater than zero.");
                return;
            }
            account.withdraw(amount, currentUser, fileManager); // Updated call
            JOptionPane.showMessageDialog(null, "Withdrawal successful! New balance: " + account.getBalance());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid amount entered. Please enter a numeric value.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    private void transfer(Account fromAccount) {
        if (currentUser.getAccounts().size() < 2) {
            JOptionPane.showMessageDialog(null, "You need at least two accounts to transfer.");
            return;
        }
    
        String toAccountNumber = (String) JOptionPane.showInputDialog(null, 
            "Transfer from account " + fromAccount.getAccountNumber() + "\n" +
            "Select account to transfer to:", 
            "Transfer", 
            JOptionPane.PLAIN_MESSAGE, 
            null, 
            currentUser.getAccounts().stream()
                .filter(account -> !account.equals(fromAccount)) // Exclude the fromAccount
                .map(Account::getAccountNumber)
                .toArray(String[]::new),
            null);
    
        if (toAccountNumber == null) return; // User canceled selection
        
        Account toAccount = currentUser.getAccounts().stream()
                .filter(account -> account.getAccountNumber().equals(toAccountNumber))
                .findFirst()
                .orElse(null);
    
        String input = JOptionPane.showInputDialog(null, "Enter amount to transfer from account " + fromAccount.getAccountNumber() + " to account " + toAccountNumber + ":");
        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(null, "Transfer amount must be greater than zero.");
                return;
            }
            fromAccount.withdraw(amount, currentUser, fileManager); // Updated call
            toAccount.deposit(amount, currentUser, fileManager); // Updated call
            JOptionPane.showMessageDialog(null, "Transfer successful!\nNew balance for " + fromAccount.getAccountNumber() + ": " + fromAccount.getBalance() + "\nNew balance for " + toAccountNumber + ": " + toAccount.getBalance());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid amount entered. Please enter a numeric value.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void logout() {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?", "Log Out", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            currentUser = null;
            JOptionPane.showMessageDialog(null, "You have been logged out.");
        }
    }

    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.start();
    }
}