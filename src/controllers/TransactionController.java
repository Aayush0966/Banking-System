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
    }  
    
    public boolean transfer(String targetAccountId, double amount) {
        if (!isValidAmount(amount)) {
            System.err.println("Transfer failed: Invalid amount " + amount);
            return false;
        }

        Account targetAccount = transactionService.findAccountById(targetAccountId);
        if (targetAccount == null) {
            System.err.println("Transfer failed: Target account not found " + targetAccountId);
            return false;
        }

        if (accountId.equals(targetAccountId)) {
            System.err.println("Transfer failed: Self transfer attempted from " + accountId + " to " + targetAccountId);
            return false;
        }

        Account sourceAccount = transactionService.findAccountById(accountId);
        if (sourceAccount == null) {
            System.err.println("Transfer failed: Source account not found " + accountId);
            return false;
        }

        if (sourceAccount.getBalance() < amount) {
            System.err.println("Transfer failed: Insufficient funds. Balance: " + sourceAccount.getBalance() + ", Amount: " + amount);
            return false;
        }        
        boolean result = transactionService.transfer(accountId, targetAccountId, amount);
        return result;
    }

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

    public String validateAmount(String amountStr) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return "Amount is required";
        }
        
        if (amountStr.indexOf('.') != amountStr.lastIndexOf('.')) {
            return "Invalid amount format. Amount cannot contain multiple decimal points";
        }

        try {
            double amount = Double.parseDouble(amountStr.trim());
            if (amount <= 0) {
                return "Amount must be positive";
            }
            
            if (amount > 1000000) {
                return "Amount exceeds maximum allowed transaction value (1,000,000)";
            }
            
            String amountString = String.valueOf(amount);
            if (amountString.contains(".")) {
                String[] parts = amountString.split("\\.");
                if (parts.length > 1 && parts[1].length() > 2) {
                    return "Amount can have at most 2 decimal places";
                }
            }
        } catch (NumberFormatException e) {
            return "Please enter a valid number";
        }

        return null; 
    }  
      public String validateTransfer(String targetAccountIdStr, String amountStr) {
        if (targetAccountIdStr == null || targetAccountIdStr.trim().isEmpty()) {
            return "Target account ID is required";
        }

        String amountValidation = validateAmount(amountStr);
        if (amountValidation != null) {
            return amountValidation;
        }

        Account targetAccount = transactionService.findAccountById(targetAccountIdStr.trim());
        if (targetAccount == null) {
            return "Target account not found";
        }

        if (accountId.equals(targetAccountIdStr.trim())) {
            return "Cannot transfer to the same account";
        }
        
        try {
            Account sourceAccount = transactionService.findAccountById(accountId);
            double amount = parseAmount(amountStr.trim());
            if (sourceAccount.getBalance() < amount) {
                return "Insufficient funds for transfer";
            }
        } catch (NumberFormatException e) {
            return "Invalid amount format";
        }

        return null;
    }

    private boolean isValidAmount(double amount) {
        if (amount <= 0) {
            return false;
        }
        
        if (amount > 1000000) {
            return false;
        }
        
        String amountStr = String.valueOf(amount);
        if (amountStr.contains(".")) {
            String[] parts = amountStr.split("\\.");
            if (parts.length > 1 && parts[1].length() > 2) {
                return false;
            }
        }
        
        return true;
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
        if (amountStr == null || amountStr.trim().isEmpty()) {
            throw new NumberFormatException("Amount cannot be empty");
        }
        
        String trimmedAmount = amountStr.trim();
        
        if (trimmedAmount.indexOf('.') != trimmedAmount.lastIndexOf('.')) {
            throw new NumberFormatException("Invalid amount format");
        }
        
        return Double.parseDouble(trimmedAmount);
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