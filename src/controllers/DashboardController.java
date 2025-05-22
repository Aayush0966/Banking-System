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

    // Customer-related business logic
    public List<Customer> getAllCustomers() {
        return transactionService.getAllCustomers();
    }

    public Customer findCustomerById(String customerId) {
        return transactionService.findCustomerById(customerId);
    }

    public boolean addCustomer(String name, String email, String phone) {
        if (name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty()) {
            return false;
        }

        Customer customer = new Customer(name.trim(), email.trim(), phone.trim());
        return transactionService.addCustomer(customer);
    }

    public boolean updateCustomer(String customerId, String name, String email, String phone) {
        if (customerId == null || name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty()) {
            return false;
        }

        Customer customer = transactionService.findCustomerById(customerId);
        if (customer == null) {
            return false;
        }

        customer.setName(name.trim());
        customer.setEmail(email.trim());
        customer.setPhone(phone.trim());

        return transactionService.updateCustomer(customer);
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

    // Account-related business logic
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

    // Utility methods
    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }

    // Validation helper methods
    public String validateCustomerData(String name, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            return "Name is required";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (phone == null || phone.trim().isEmpty()) {
            return "Phone is required";
        }
        return null; // No validation errors
    }

    public String validateAccountData(String accountName, String initialDepositStr) {
        if (accountName == null || accountName.trim().isEmpty()) {
            return "Account name is required";
        }
        if (initialDepositStr == null || initialDepositStr.trim().isEmpty()) {
            return "Initial deposit amount is required";
        }

        try {
            double initialDeposit = Double.parseDouble(initialDepositStr.trim());
            if (initialDeposit < 0) {
                return "Initial deposit cannot be negative";
            }
        } catch (NumberFormatException e) {
            return "Invalid deposit amount";
        }

        return null; // No validation errors
    }
}