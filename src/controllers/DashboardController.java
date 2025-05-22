package controllers;

import model.Account;
import model.Customer;
import services.TransactionService;
import java.util.List;
import java.util.Random;

public class DashboardController {
    private final TransactionService transactionService;

    public DashboardController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public List<Customer> getAllCustomers() {
        return transactionService.getAllCustomers();
    }

    public Customer findCustomerById(String customerId) {
        return transactionService.findCustomerById(customerId);
    }

    public boolean addCustomer(String name, String email, String phone) {
        String validationError = validateCustomerData(name, email, phone);
        if (validationError != null) {
            return false;
        }

        try {
            Customer customer = new Customer(name.trim(), email.trim(), phone.trim());
            return transactionService.addCustomer(customer);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean updateCustomer(String customerId, String name, String email, String phone) {
        if (customerId == null) {
            return false;
        }
        
        String validationError = validateCustomerData(name, email, phone);
        if (validationError != null) {
            return false;
        }

        Customer customer = transactionService.findCustomerById(customerId);
        if (customer == null) {
            return false;
        }

        try {
            customer.setName(name.trim());
            customer.setEmail(email.trim());
            customer.setPhone(phone.trim());
            return transactionService.updateCustomer(customer);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean canRemoveCustomer(String customerId) {
        Customer customer = transactionService.findCustomerById(customerId);
        return customer != null && !customer.hasActiveAccounts();
    }

    public boolean removeCustomer(String customerId) {
        Customer customer = transactionService.findCustomerById(customerId);
        if (customer == null || customer.hasActiveAccounts()) {
            return false;
        }
        return transactionService.deleteCustomer(customerId);
    }

    public List<Customer> getCustomersWithAccounts() {
        return transactionService.getAllCustomers().stream()
                .filter(customer -> !customer.getAccounts().isEmpty())
                .toList();
    }

    public Account findAccountById(String accountId) {
        return transactionService.findAccountById(accountId);
    }

    public boolean addAccount(String customerId, String accountName, double initialDeposit) {
        if (customerId == null || accountName == null || accountName.trim().isEmpty() || initialDeposit < 0) {
            return false;
        }

        Customer customer = transactionService.findCustomerById(customerId);
        if (customer == null) {
            return false;
        }

        String accountNumber = generateAccountNumber();
        Account account = new Account(accountName.trim(), accountNumber, customerId);

        boolean accountAdded = transactionService.addAccount(customerId, account);
        if (accountAdded && initialDeposit > 0) {
            transactionService.deposit(account.getId(), initialDeposit);
        }

        return accountAdded;
    }

    public boolean removeAccount(String accountId) {
        Account account = transactionService.findAccountById(accountId);
        if (account == null) {
            return false;
        }
        return transactionService.deleteAccount(account.getCustomerId(), accountId);
    }

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }

    public String validateCustomerData(String name, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            return "Name is required";
        }
        
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            return "Invalid email format. Please enter a valid email address";
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            return "Phone is required";
        }
        
        String digitsOnly = phone.replaceAll("[^0-9]", "");
        
        if (digitsOnly.length() != 10) {
            return "Phone number must be exactly 10 digits";
        }
        
        return null; 
    }

    public String validateAccountData(String accountName, String initialDepositStr) {
        if (accountName == null || accountName.trim().isEmpty()) {
            return "Account name is required";
        }
        if (initialDepositStr == null || initialDepositStr.trim().isEmpty()) {
            return "Initial deposit amount is required";
        }
        
        if (initialDepositStr.indexOf('.') != initialDepositStr.lastIndexOf('.')) {
            return "Invalid deposit amount format. Amount cannot contain multiple decimal points";
        }

        try {
            double initialDeposit = Double.parseDouble(initialDepositStr.trim());
            if (initialDeposit < 0) {
                return "Initial deposit cannot be negative";
            }
            
            String amountString = String.valueOf(initialDeposit);
            if (amountString.contains(".")) {
                String[] parts = amountString.split("\\.");
                if (parts.length > 1 && parts[1].length() > 2) {
                    return "Amount can have at most 2 decimal places";
                }
            }
        } catch (NumberFormatException e) {
            return "Invalid deposit amount";
        }

        return null; 
    }
}