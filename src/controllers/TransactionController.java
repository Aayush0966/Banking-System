package controllers;

import model.Account;
import model.Customer;
import model.Transaction;
import services.TransactionService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionController {
    private final TransactionService transactionService;
    private String accountId;

    public TransactionController(TransactionService transactionService, String accountId) {
        this.transactionService = transactionService;
        this.accountId = accountId;
    }

    // Transaction data retrieval
    public List<Transaction> getTransactions() {
        return transactionService.getTransactionsByAccount(accountId);
    }

    public Account getCurrentAccount() {
        return transactionService.findAccountById(accountId);
    }

    public Customer getAccountOwner() {
        Account account = getCurrentAccount();
        if (account != null) {
            return transactionService.findCustomerById(account.getCustomerId());
        }
        return null;
    }

    // Transaction processing business logic
    public boolean deposit(double amount) {
        if (!isValidAmount(amount)) {
            return false;
        }
        return transactionService.deposit(accountId, amount);
    }

    public boolean withdraw(double amount) {
        if (!isValidAmount(amount)) {
            return false;
        }
        return transactionService.withdraw(accountId, amount);
    }    public boolean transfer(String targetAccountId, double amount) {
        if (!isValidAmount(amount)) {
            System.err.println("Transfer failed: Invalid amount " + amount);
            return false;
        }

        // Validate target account exists
        Account targetAccount = transactionService.findAccountById(targetAccountId);
        if (targetAccount == null) {
            System.err.println("Transfer failed: Target account not found " + targetAccountId);
            return false;
        }

        // Prevent self-transfer
        if (accountId.equals(targetAccountId)) {
            System.err.println("Transfer failed: Self transfer attempted from " + accountId + " to " + targetAccountId);
            return false;
        }

        // Get source account to check balance
        Account sourceAccount = transactionService.findAccountById(accountId);
        if (sourceAccount == null) {
            System.err.println("Transfer failed: Source account not found " + accountId);
            return false;
        }

        // Check if source account has enough funds
        if (sourceAccount.getBalance() < amount) {
            System.err.println("Transfer failed: Insufficient funds. Balance: " + sourceAccount.getBalance() + ", Amount: " + amount);
            return false;
        }        // Log the transfer attempt with debug info
        System.out.println("Attempting transfer: From " + accountId + " to " + targetAccountId + " amount: " + amount);
        System.out.println("Source account: " + sourceAccount.getName() + " (" + sourceAccount.getId() + "), balance: " + sourceAccount.getBalance());
        System.out.println("Target account: " + targetAccount.getName() + " (" + targetAccount.getId() + "), balance: " + targetAccount.getBalance());
        
        boolean result = transactionService.transfer(accountId, targetAccountId, amount);
        System.out.println("Transfer result: " + (result ? "Success" : "Failed"));
        return result;
    }

    // Transaction formatting for UI display
    public Object[] formatTransactionForDisplay(Transaction transaction) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromTo = determineTransactionDirection(transaction);

        return new Object[]{
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                dateFormat.format(transaction.getTimeStamp()),
                fromTo
        };
    }

    // Validation methods
    public String validateAmount(String amountStr) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return "Amount is required";
        }

        try {
            double amount = Double.parseDouble(amountStr.trim());
            if (amount <= 0) {
                return "Amount must be positive";
            }
        } catch (NumberFormatException e) {
            return "Please enter a valid number";
        }

        return null; // No validation errors
    }    public String validateTransfer(String targetAccountIdStr, String amountStr) {
        if (targetAccountIdStr == null || targetAccountIdStr.trim().isEmpty()) {
            return "Target account ID is required";
        }

        String amountValidation = validateAmount(amountStr);
        if (amountValidation != null) {
            return amountValidation;
        }

        // No need to try to parse as long since the IDs are strings
        
        // Check if target account exists
        Account targetAccount = transactionService.findAccountById(targetAccountIdStr.trim());
        if (targetAccount == null) {
            return "Target account not found";
        }

        // Check for self-transfer
        if (accountId.equals(targetAccountIdStr.trim())) {
            return "Cannot transfer to the same account";
        }
        
        // Check if the source account has enough funds
        Account sourceAccount = transactionService.findAccountById(accountId);
        double amount = Double.parseDouble(amountStr.trim());
        if (sourceAccount.getBalance() < amount) {
            return "Insufficient funds for transfer";
        }

        return null;
    }

    // Helper methods
    private boolean isValidAmount(double amount) {
        return amount > 0;
    }

    private String determineTransactionDirection(Transaction transaction) {
        Account account = getCurrentAccount();
        if (account == null) return "";

        return switch (transaction.getType()) {
            case "Deposit" -> "External → " + account.getAccountNum();
            case "Withdrawal" -> account.getAccountNum() + " → External";
            case "Transfer" -> {
                if (accountId.equals(transaction.getSendingAccountId())) {
                    Account target = transactionService.findAccountById(transaction.getReceivingAccountId());
                    yield account.getAccountNum() + " → " + (target != null ? target.getAccountNum() : "Unknown");
                } else {
                    Account source = transactionService.findAccountById(transaction.getSendingAccountId());
                    yield (source != null ? source.getAccountNum() : "Unknown") + " → " + account.getAccountNum();
                }
            }
            default -> "";
        };
    }

    public String getAccountDisplayInfo() {
        Account account = getCurrentAccount();
        Customer customer = getAccountOwner();

        if (account == null || customer == null) {
            return "Account information unavailable";
        }

        return "Account: " + account.getName() + " (" + account.getAccountNum() + ")" +
                " | Customer: " + customer.getName();
    }

    public String getBalanceDisplayText() {
        Account account = getCurrentAccount();
        return account != null ? "Balance: $" + account.getBalance() : "Balance: N/A";
    }

    public double parseAmount(String amountStr) throws NumberFormatException {
        return Double.parseDouble(amountStr.trim());
    }

    public List<Customer> getAllCustomers() {
        return transactionService.getAllCustomers();
    }

    public List<Account> getAllAccounts() {
        return transactionService.getAllAccounts();
    }

    public String getCurrentAccountId() {
        return accountId;
    }


    public long parseAccountId(String accountIdStr) throws NumberFormatException {
        return Long.parseLong(accountIdStr.trim());
    }

    public List<Account> getAccountsByCustomerId(String customerId) {
        return transactionService.getAccountsByCustomerId(customerId);
    }
}