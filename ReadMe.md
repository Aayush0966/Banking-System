# Banking System - Admin Application

## Overview
********This Banking System is an administrative application designed as a college assignment for bank employees to manage customer accounts and transactions.********
## Features
- **Secure Admin Login**: Protected access for authorized personnel only
- **Customer Management**: Add, edit, and delete customer records
- **Account Management**: Create and manage customer accounts
- **Transaction Processing**: Handle deposits, withdrawals, and transfers
- **Data Persistence**: All data is securely stored and maintained
- **Error Handling**: Robust error management throughout the application



## Installation
1. Extract the application files to your preferred directory
2. Ensure Java is properly installed on your system
3. Run the application using the command: `java -jar BankingSystem.jar`

## Default Login Credentials
- **Username**: admin
- **Password**: admin123

## File Structure
```
src/
├── Main.java                     # Application entry point
├── exceptions/                   # Custom exception classes
│   ├── FileReadException.java    # File handling errors
│   ├── InsufficientFundsException.java # Insufficient balance errors
│   └── InvalidDataException.java # Data validation errors
├── interfaces/                   # Application interfaces
│   ├── IAuthService.java         # Authentication interface
│   └── IFileHandler.java         # File operations interface
├── model/                        # Data models
│   ├── BankEntity.java           # Base entity class
│   ├── Customer.java             # Customer entity
│   ├── Account.java              # Account entity
│   └── Transaction.java          # Transaction entity
├── service/                      # Business logic
│   ├── AuthService.java          # Authentication implementation
│   ├── FileHandler.java          # File operations implementation
│   └── TransactionService.java   # Transaction processing
└── ui/                           # User interface components
    ├── BaseFrame.java            # Base UI frame
    ├── LoginUI.java              # Login screen
    ├── DashboardUI.java          # Main dashboard
    └── TransactionUI.java        # Transaction management
```

## Usage Guidelines
1. **Login**: Enter your administrator credentials
2. **Dashboard**: View all customers and accounts
3. **Customer Management**: Add new customers, edit details, or remove records
4. **Account Operations**: Create accounts, process transactions, and view history

## Data Storage
The application stores all data locally in serialized files:
- customers.csv - Customer records
- transactions.csv - Transaction history
- accounts.csv - Account details

