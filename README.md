# ATM System

An ATM system project that simulates basic banking operations for users and administrators. The system allows users to manage accounts, perform transactions, and store account information using file-based data storage.

## Features

- User login
- Admin login
- Create and manage user accounts
- Checking account support
- Savings account support
- Deposit money
- Withdraw money
- Transfer money
- View account balance
- View transaction history
- Store user data in text files
- Store transaction records in text files
- Generate reports

## User Roles

### User

Regular users can:

- Log in to their account
- View account information
- Check account balance
- Deposit money
- Withdraw money
- Transfer money between accounts
- View transaction history
- Manage checking and savings accounts

### Admin

Admins can:

- Log in to the admin panel
- View user accounts
- Manage user information
- View transaction records
- Generate reports
- Monitor system activity

## Technologies Used

- Java
- Object-Oriented Programming
- File handling
- Text file storage
- VS Code

## Project Structure

    atm-system/
    ├── bin/
    │   ├── Account.class
    │   ├── Admin.class
    │   ├── ATM.class
    │   ├── CheckingAccount.class
    │   ├── FileManager.class
    │   ├── Main.class
    │   ├── SavingsAccount.class
    │   ├── Transaction.class
    │   ├── User.class
    │   ├── reports.txt
    │   ├── transactions.txt
    │   └── users.txt
    ├── src/
    │   ├── Account.java
    │   ├── Admin.java
    │   ├── ATM.java
    │   ├── CheckingAccount.java
    │   ├── FileManager.java
    │   ├── Main.java
    │   ├── SavingsAccount.java
    │   ├── Transaction.java
    │   ├── User.java
    │   ├── transactions.txt
    │   └── users.txt
    └── README.md

## Main Files

- `Main.java` - Starts the ATM system
- `ATM.java` - Handles the main ATM operations and menu flow
- `User.java` - Represents regular users in the system
- `Admin.java` - Represents admin users and admin functions
- `Account.java` - Base account class
- `CheckingAccount.java` - Represents checking account behavior
- `SavingsAccount.java` - Represents savings account behavior
- `Transaction.java` - Stores transaction details
- `FileManager.java` - Handles reading from and writing to text files
- `users.txt` - Stores user account information
- `transactions.txt` - Stores transaction records
- `reports.txt` - Stores generated reports

## How to Run the Project

1. Clone or download this repository.
2. Open the project folder in VS Code or another Java IDE.
3. Make sure Java is installed on your computer.
4. Open the `src` folder.
5. Run the main file:

    `Main.java`

Or run it from Terminal:

    javac src/*.java
    java -cp src Main

## Notes

- This project was developed as a Java-based ATM simulation system.
- The system uses object-oriented programming concepts such as classes, inheritance, and encapsulation.
- Text files are used to store users, transactions, and reports.
- The `src` folder contains the main Java source code.
- The `bin` folder contains compiled `.class` files.

## Authors

- Ghala Alghamdi
- Hiba Amanulla
- Effat University
- Computer Science Department
Course: CS2132 – Machine Learning
Instructor: Dr. Fidaa Abed
