
public class SavingsAccount extends Account {
    private double interestRate;

    // Constructor for SavingsAccount
    public SavingsAccount(String accountNumber, double initialBalance, double interestRate) {
        super(accountNumber, initialBalance);
        this.interestRate = interestRate;
    }

    // Apply interest to the balance
    public void applyInterest(User user, FileManager fileManager) {
        double interest = getBalance() * (interestRate / 100);
        super.deposit(interest, user, fileManager); // Corrected call
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    public double getInterestRate() {
        return interestRate; // Getter for interest rate
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate; // Setter for interest rate
    }
}