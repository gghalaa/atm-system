import java.awt.*;
import java.util.List;
import javax.swing.*;

public class Admin {
    private List<User> users;
    private FileManager fileManager;

    public Admin(List<User> users) {
        this.users = users;
        this.fileManager = new FileManager();
    }

    // Manage user accounts
    public void manageAccounts() {
        JPasswordField pinField = new JPasswordField();
        int option = JOptionPane.showConfirmDialog(null, pinField,
                "Enter User PIN:", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String enteredPin = new String(pinField.getPassword());
            User selectedUser = findUserByPin(enteredPin);
            if (selectedUser != null) {
                manageSelectedUser(selectedUser);
            } else {
                JOptionPane.showMessageDialog(null, "No user found with that PIN.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "No action taken.");
        }
    }

    // Manage actions for the selected user
    public void manageSelectedUser(User user) {
        String action;
        do {
            action = JOptionPane.showInputDialog(null,
                    "Choose an action:\n1. Reset PIN\n2. Unlock Account\nEnter 1 or 2:");
        } while (!"1".equals(action) && !"2".equals(action));

        if ("1".equals(action)) {
            resetPin(user);
        } else if ("2".equals(action)) {
            unlockAccount(user);
        }
    }

    // Find user by PIN
    private User findUserByPin(String pin) {
        for (User user : users) {
            if (user.getPin().equals(pin)) {
                return user;
            }
        }
        return null;
    }

    // Reset the user's PIN
    private void resetPin(User user) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // New PIN field
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Enter New PIN:"), gbc);
        JPasswordField newPinField = new JPasswordField(20);
        gbc.gridx = 1; panel.add(newPinField, gbc);

        // Confirm PIN field
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Confirm New PIN:"), gbc);
        JPasswordField confirmPinField = new JPasswordField(20);
        gbc.gridx = 1; panel.add(confirmPinField, gbc);

        // Set the dialog
        int result = JOptionPane.showConfirmDialog(null, panel, "Reset PIN",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String newPin = new String(newPinField.getPassword()).trim();
            String confirmPin = new String(confirmPinField.getPassword()).trim();

            if (!newPin.equals(confirmPin)) {
                JOptionPane.showMessageDialog(null, "PINs do not match. Please try again.");
                return;
            }

            user.setPin(newPin); // Assuming User has a setPin method
            JOptionPane.showMessageDialog(null, "PIN reset successfully.");
        }
    }

    // Unlock the user's account
    private void unlockAccount(User user) {
        user.setAccountLocked(false); // Unlock the account
        JOptionPane.showMessageDialog(null,
                "Account unlocked for User ID: " + user.getUserID());
        fileManager.saveUsers(users); // Save updated user list
    }

    // Generate user report
    public void generateUserReport(User user) {
        StringBuilder report = new StringBuilder();
        report.append("User Report for ").append(user.getName()).append(" (")
              .append(user.getUserID()).append("):\n");
        for (Account account : user.getAccounts()) {
            report.append("Account Number: ").append(account.getAccountNumber())
                  .append(", Balance: ").append(account.getBalance()).append("\n");
        }
        report.append("Transaction History:\n");
        for (Transaction transaction : user.getTransactionHistory()) {
            report.append(transaction.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(null, report.toString());
    }
}