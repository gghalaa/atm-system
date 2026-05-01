import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Main {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private User currentUser;
    private List<User> users;
    private FileManager fileManager;

    public Main() {
        frame = new JFrame("ATM System");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        users = new ArrayList<>();
        fileManager = new FileManager();
        users = fileManager.loadUsers(); // Load users from file

        mainPanel.add(createLoginPage(), "Login");
        mainPanel.add(createFunctionalityPage(), "Functionality");
        mainPanel.add(createAdminPanel(), "Admin Panel");

        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private JPanel createLoginPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        JTextField userIDField = new JTextField(20);
        JPasswordField pinField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1; panel.add(userIDField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("PIN:"), gbc);
        gbc.gridx = 1; panel.add(pinField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(loginButton, gbc);
        gbc.gridy = 3; panel.add(registerButton, gbc);

        loginButton.addActionListener(e -> login(userIDField.getText(), new String(pinField.getPassword())));
        registerButton.addActionListener(e -> registerUser());
        return panel;
    }

    private JPanel createFunctionalityPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JComboBox<String> accountComboBox = new JComboBox<>();
        JLabel balanceLabel = new JLabel("Balance: N/A");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(new JLabel("Select Account:"), gbc);
        gbc.gridy = 1;
        panel.add(accountComboBox, gbc);
        gbc.gridy = 2;
        panel.add(balanceLabel, gbc);

        accountComboBox.addActionListener(e -> {
            String selectedAccountNumber = (String) accountComboBox.getSelectedItem();
            Account selectedAccount = getAccountByNumber(selectedAccountNumber);
            if (selectedAccount != null) {
                balanceLabel.setText("Balance: " + selectedAccount.getBalance());
            } else {
                balanceLabel.setText("Balance: N/A");
            }
        });

        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton logoutButton = new JButton("Log Out");
        JButton adminPanelButton = new JButton("Admin Panel");

        gbc.gridy = 3; panel.add(depositButton, gbc);
        gbc.gridy = 4; panel.add(withdrawButton, gbc);
        gbc.gridy = 5; panel.add(transferButton, gbc);
        gbc.gridy = 6; panel.add(logoutButton, gbc);
        gbc.gridy = 7; panel.add(adminPanelButton, gbc);

        depositButton.addActionListener(e -> deposit((String) accountComboBox.getSelectedItem(), balanceLabel));
        withdrawButton.addActionListener(e -> withdraw((String) accountComboBox.getSelectedItem(), balanceLabel));
        transferButton.addActionListener(e -> transfer((String) accountComboBox.getSelectedItem(), balanceLabel));
        logoutButton.addActionListener(e -> logout());
        adminPanelButton.addActionListener(e -> cardLayout.show(mainPanel, "Admin Panel"));

        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton createAccountButton = new JButton("Create New Account");
        JButton accountManagementButton = new JButton("Account Management");
        JButton generateReportsButton = new JButton("Generate Reports");
        JButton backButton = new JButton("Back");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(createAccountButton, gbc);
        gbc.gridy = 1; panel.add(accountManagementButton, gbc);
        gbc.gridy = 2; panel.add(generateReportsButton, gbc);
        gbc.gridy = 3; panel.add(backButton, gbc);

        createAccountButton.addActionListener(e -> createNewAccount());
        accountManagementButton.addActionListener(e -> manageAccountsByPin());
        generateReportsButton.addActionListener(e -> {
            if (currentUser != null) {
                showUserReport(currentUser); // Show report for the current user
            } else {
                JOptionPane.showMessageDialog(frame, "No user is currently logged in.");
            }
        });
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Functionality"));

        return panel;
    }

    private void showUserReport(User user) {
        StringBuilder report = new StringBuilder();
        
        report.append("User Report for ").append(user.getName()).append(" (").append(user.getUserID()).append("):\n");
        
        // Adding account details
        for (Account account : user.getAccounts()) {
            report.append("Account Number: ").append(account.getAccountNumber())
                  .append(", Balance: ").append(account.getBalance()).append("\n");
        }
        
        report.append("Transaction History:\n");
        
        // Assuming user has a method to get transaction history
        for (Transaction transaction : user.getTransactionHistory()) {
            report.append(transaction.toString()).append("\n");
        }
        
        // Display the report in a dialog
        JOptionPane.showMessageDialog(frame, report.toString(), "User Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void login(String userID, String pin) {
        for (User user : users) {
            if (user.getUserID().equals(userID) && user.getPin().equals(pin)) {
                currentUser = user;
                updateAccountComboBox((JComboBox<String>) ((JPanel) mainPanel.getComponent(1)).getComponent(1));
                cardLayout.show(mainPanel, "Functionality");
                return;
            }
        }
        JOptionPane.showMessageDialog(frame, "Invalid User ID or PIN.");
    }

    private void updateAccountComboBox(JComboBox<String> accountComboBox) {
        accountComboBox.removeAllItems(); // Clear existing items
        for (Account account : currentUser.getAccounts()) {
            accountComboBox.addItem(account.getAccountNumber()); // Add account numbers to the combo box
        }
    }

    private void registerUser() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField nameField = new JTextField(20);
        JTextField userIDField = new JTextField(20);
        JPasswordField pinField = new JPasswordField(20);
        JTextField initialBalanceField = new JTextField(20);
        String[] accountTypes = {"Checking Account", "Savings Account"};
        JComboBox<String> accountTypeComboBox = new JComboBox<>(accountTypes);

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

        int result = JOptionPane.showConfirmDialog(frame, panel, "Register",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String userID = userIDField.getText().trim();
            String pin = new String(pinField.getPassword()).trim();
            String accountType = (String) accountTypeComboBox.getSelectedItem();
            String initialBalanceString = initialBalanceField.getText().trim();

            if (name.isEmpty() || userID.isEmpty() || pin.isEmpty() || initialBalanceString.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields must be filled.");
                return;
            }

            double initialBalance;
            try {
                initialBalance = Double.parseDouble(initialBalanceString);
                if (initialBalance < 0) {
                    JOptionPane.showMessageDialog(frame, "Initial balance must be a positive number.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Initial balance must be a valid number.");
                return;
            }

            for (User user : users) {
                if (user.getUserID().equals(userID)) {
                    JOptionPane.showMessageDialog(frame, "User ID already exists. Please choose another.");
                    return;
                }
            }

            String accountNumber = (accountType.equals("Checking Account") ? "C-ACC" : "S-ACC") + System.currentTimeMillis();
            Account newAccount = createAccount(accountType, accountNumber, initialBalance);
            User newUser = new User(name, userID, pin);
            newUser.addAccount(newAccount);
            users.add(newUser);
            fileManager.saveUsers(users);
            JOptionPane.showMessageDialog(frame, "Registration successful! You can now log in.");
        }
    }

    private Account createAccount(String accountType, String accountNumber, double initialBalance) {
        if (accountType.equals("Checking Account")) {
            return new CheckingAccount(accountNumber, initialBalance, 100);
        } else {
            return new SavingsAccount(accountNumber, initialBalance, 1.5);
        }
    }

    private void deposit(String accountNumber, JLabel balanceLabel) {
        Account account = getAccountByNumber(accountNumber);
        if (account != null) {
            double amount = getValidatedAmount("Enter amount to deposit into account " + accountNumber + ":");
            if (isValidAmount(amount)) {
                boolean success = account.deposit(amount, currentUser, fileManager);
                if (success) {
                    logTransaction("Deposit", amount, accountNumber);
                    JOptionPane.showMessageDialog(frame, "Deposit successful! New balance: " + account.getBalance());
                    balanceLabel.setText("Balance: " + account.getBalance());
                } else {
                    JOptionPane.showMessageDialog(frame, "Deposit failed. Please try again.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "No accounts available for deposit.");
        }
    }

    private void withdraw(String accountNumber, JLabel balanceLabel) {
        Account account = getAccountByNumber(accountNumber);
        if (account != null) {
            double amount = getValidatedAmount("Enter amount to withdraw from account " + accountNumber + ":");
            if (isValidAmount(amount) && amount <= account.getBalance()) {
                boolean success = account.withdraw(amount, currentUser, fileManager);
                if (success) {
                    logTransaction("Withdrawal", amount, accountNumber);
                    JOptionPane.showMessageDialog(frame, "Withdrawal successful! New balance: " + account.getBalance());
                    balanceLabel.setText("Balance: " + account.getBalance());
                } else {
                    JOptionPane.showMessageDialog(frame, "Withdrawal failed. Please try again.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid withdrawal amount.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "No accounts available for withdrawal.");
        }
    }

    private void transfer(String accountNumber, JLabel balanceLabel) {
        Account account = getAccountByNumber(accountNumber);
        if (currentUser != null && currentUser.getAccounts().size() >= 2) {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (Account acc : currentUser.getAccounts()) {
                if (!acc.getAccountNumber().equals(accountNumber)) {
                    listModel.addElement(acc.getAccountNumber());
                }
            }

            JList<String> accountList = new JList<>(listModel);
            accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(accountList);

            double amount = getValidatedAmount("Enter amount to transfer from account " + accountNumber + ":");
            if (isValidAmount(amount) && amount <= account.getBalance()) {
                int result = JOptionPane.showConfirmDialog(frame, scrollPane, "Select Target Account", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String selectedAccountNumber = accountList.getSelectedValue();
                    if (selectedAccountNumber != null) {
                        Account targetAccount = getAccountByNumber(selectedAccountNumber);
                        boolean withdrawalSuccess = account.withdraw(amount, currentUser, fileManager);
                        boolean depositSuccess = targetAccount.deposit(amount, currentUser, fileManager);

                        if (withdrawalSuccess && depositSuccess) {
                            JOptionPane.showMessageDialog(frame, "Transfer successful!");
                            logTransaction("Transfer Out", amount, accountNumber);
                            logTransaction("Transfer In", amount, selectedAccountNumber);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Transfer failed. Please try again.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "No target account selected.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid transfer amount.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "You need at least two accounts to transfer.");
        }
    }

    private void createNewAccount() {
        JPanel panel = new JPanel(new GridLayout(0, 1));

        JRadioButton checkingButton = new JRadioButton("Checking Account");
        JRadioButton savingsButton = new JRadioButton("Savings Account");
        ButtonGroup accountTypeGroup = new ButtonGroup();
        accountTypeGroup.add(checkingButton);
        accountTypeGroup.add(savingsButton);

        panel.add(new JLabel("Select Account Type:"));
        panel.add(checkingButton);
        panel.add(savingsButton);

        JTextField initialBalanceField = new JTextField();
        panel.add(new JLabel("Enter Initial Balance:"));
        panel.add(initialBalanceField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Create New Account", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String accountType = checkingButton.isSelected() ? "Checking Account" : "Savings Account";
            String accountNumber = (accountType.equals("Checking Account") ? "C-ACC" : "S-ACC") + System.currentTimeMillis();

            String initialBalanceString = initialBalanceField.getText().trim();
            if (initialBalanceString.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Initial balance cannot be empty.");
                return;
            }

            double initialBalance;
            try {
                initialBalance = Double.parseDouble(initialBalanceString);
                if (initialBalance < 0) {
                    JOptionPane.showMessageDialog(frame, "Initial balance must be a positive number.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid amount. Please enter a numeric value.");
                return;
            }

            Account newAccount = createAccount(accountType, accountNumber, initialBalance);
            currentUser.addAccount(newAccount);
            fileManager.saveUsers(users);
            JOptionPane.showMessageDialog(frame, "New account created! Account Number: " + accountNumber);
        }
    }

    private void manageAccountsByPin() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        JPasswordField pinField = new JPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Enter User PIN:"), gbc);
        gbc.gridx = 1; panel.add(pinField, gbc);

        if (currentUser != null) {
            gbc.gridx = 0; gbc.gridy = 1; 
            panel.add(new JLabel("Account Name: " + currentUser.getName()), gbc);
            gbc.gridy = 2; 
            panel.add(new JLabel("User ID: " + currentUser.getUserID()), gbc);
        }

        int result = JOptionPane.showConfirmDialog(frame, panel, "Account Management", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String pin = new String(pinField.getPassword()).trim();
            if (currentUser.getPin().equals(pin)) {
                String[] options = {"Change PIN", "Unlock Account"};
                int choice = JOptionPane.showOptionDialog(frame, "Select an action:", "Account Management",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                
                if (choice == -1) return; // User closed the dialog

                if (choice == 0) { // Change PIN
                    JPanel newPinPanel = new JPanel(new GridBagLayout());
                    JPasswordField newPinField = new JPasswordField(20);
                    JPasswordField confirmPinField = new JPasswordField(20);
                    
                    gbc.gridx = 0; gbc.gridy = 0; 
                    newPinPanel.add(new JLabel("Enter New PIN:"), gbc);
                    gbc.gridx = 1; newPinPanel.add(newPinField, gbc);
                    
                    gbc.gridx = 0; gbc.gridy = 1; 
                    newPinPanel.add(new JLabel("Confirm New PIN:"), gbc);
                    gbc.gridx = 1; newPinPanel.add(confirmPinField, gbc);

                    int newPinResult = JOptionPane.showConfirmDialog(frame, newPinPanel, "Change PIN", JOptionPane.OK_CANCEL_OPTION);
                    if (newPinResult == JOptionPane.OK_OPTION) {
                        String newPin = new String(newPinField.getPassword()).trim();
                        String confirmPin = new String(confirmPinField.getPassword()).trim();

                        if (newPin.equals(confirmPin)) {
                            fileManager.updateUserPassword(currentUser.getUserID(), newPin);
                            currentUser.setPin(newPin);
                            JOptionPane.showMessageDialog(frame, "PIN updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(frame, "New PINs do not match. Please try again.");
                        }
                    }
                } else if (choice == 1) { // Unlock Account
                    // Placeholder for unlock account logic
                    JOptionPane.showMessageDialog(frame, "Account unlocked successfully!");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect PIN.");
            }
        }
    }

    private void logout() {
        currentUser = null;
        cardLayout.show(mainPanel, "Login");
    }

    private Account getAccountByNumber(String accountNumber) {
        if (currentUser != null) {
            for (Account account : currentUser.getAccounts()) {
                if (account.getAccountNumber().equals(accountNumber)) {
                    return account;
                }
            }
        }
        return null;
    }

    private double getValidatedAmount(String message) {
        String input = JOptionPane.showInputDialog(frame, message);
        if (input != null) {
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid amount. Please enter a numeric value.");
            }
        }
        return 0;
    }

    private boolean isValidAmount(double amount) {
        return amount > 0;
    }

    private void logTransaction(String type, double amount, String accountNumber) {
        if (currentUser != null) {
            Transaction transaction = new Transaction(currentUser.getUserID(), accountNumber, type, amount);
            currentUser.addTransaction(transaction); // Assuming User class has this method
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}