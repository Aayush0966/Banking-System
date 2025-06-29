package services;

import exceptions.FileReadException;
import exceptions.InsufficientFundsException;
import exceptions.InvalidDataException;
import model.Account;
import model.Customer;
import model.Transaction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionService {
    private final CustomerCSVHandler customerFileHandler;
    private final TransactionCSVHandler transactionFileHandler;
    private final AccountCSVHandler accountFileHandler;
    private List<Customer> customers;
    private List<Transaction> transactions;
    private List<Account> accounts;

    public TransactionService() {
        accountFileHandler = new AccountCSVHandler("accounts.csv");
        customerFileHandler = new CustomerCSVHandler("customers.csv");
        transactionFileHandler = new TransactionCSVHandler("transactions.csv");
        loadDataFromFile();
    }


    private void loadDataFromFile() {
        try {
            customers = customerFileHandler.loadData();
            transactions = transactionFileHandler.loadData();
            accounts = accountFileHandler.loadData();

            removeDuplicateTransactions();

            for (Account account : accounts) {
             Customer owner = findCustomerById(account.getCustomerId());
             if (owner != null) {
                 owner.addAccount(account);
             }
            }
        } catch (FileReadException | InvalidDataException e) {
            System.err.println("Error loading data: " + e.getMessage());
            customers = new ArrayList<>();
            transactions = new ArrayList<>();
            accounts = new ArrayList<>();
        }
    }
    
    private void removeDuplicateTransactions() {
        List<Transaction> uniqueTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            boolean isDuplicate = false;
            for (Transaction uniqueTransaction : uniqueTransactions) {
                if (transaction.getId().equals(uniqueTransaction.getId())) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                uniqueTransactions.add(transaction);
            }
        }
        transactions = uniqueTransactions;
    }

    private void saveDataToFile() {
        try {
            customerFileHandler.saveData(customers);
            transactionFileHandler.saveData(transactions);
            accountFileHandler.saveData(accounts);
        } catch (FileReadException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts);
    }


    public Customer findCustomerById(String id) {
        for (Customer customer: customers) {
            if (customer.getId().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    public Account findAccountById(String id) {
        for (Customer customer : customers) {
            for (Account account: customer.getAccounts()) {
                if (account.getId().equals(id)) {
                    return account;
                }
            }
        }
        return null;
    }

    public Account FindAccountByNumber (String accountNumber) {
        for (Customer customer: customers) {
            for (Account account: customer.getAccounts()) {
                if (account.getAccountNum().equals(accountNumber)) {
                    return account;
                }
            }
        }
        return null;
    }

    public boolean addCustomer(Customer customer) {
        if (customer == null) return false;
        customers.add(customer);
        saveDataToFile();
        return true;
    }

    public boolean updateCustomer(Customer customer) {
        if (customer == null) return false;
        for (Customer person: customers) {
            if (person.getId().equals(customer.getId())) {
                int index = customers.indexOf(person);
                customers.set(index, customer);
                saveDataToFile();
                return true;
            }

        }
        return false;
    }

    public boolean deleteCustomer(String customerId) {
        Customer customer = findCustomerById(customerId);
        if (customer != null) {
            customers.remove(customer);
            accounts.addAll(customer.getAccounts());
            saveDataToFile();
            return true;
        }
        return false;
    }

    public boolean addAccount(String customerId, Account account) {
        System.out.println(customerId + " " + account.getAccountNum());
        Customer customer = findCustomerById(customerId);
        if (customer != null) {
            accounts.add(account);
            customer.addAccount(account);
            saveDataToFile();
            return true;
        }
        return false;
    }

    public boolean deleteAccount(String customerId, String accountId) {
        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            return false;
        }
        Account account = findAccountById(accountId);
        if (account == null) {
            return false;
        }
        customer.removeAccount(account);
        accounts.remove(account); 
        saveDataToFile();
        return true;
    }

    public boolean deposit(String accountId, double amount) {
        Account account = findAccountById(accountId);
        if (account == null) return false;
        try {
            int initialSize = account.getTransactions().size();
            account.deposit(amount);
            List<Transaction> accountTransactions = account.getTransactions();
            if (accountTransactions.size() > initialSize) {
                Transaction newTransaction = accountTransactions.get(accountTransactions.size() - 1);
                transactions.add(newTransaction);
            }
            saveDataToFile();
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Deposit error: " + e.getMessage());
            return false;
        }
    }

    public boolean withdraw(String accountId, double amount) {
        Account account = findAccountById(accountId);
        if (account == null) return false;
        try {
            int initialSize = account.getTransactions().size()
            account.withdraw(amount);
            
            List<Transaction> accountTransactions = account.getTransactions();
            if (accountTransactions.size() > initialSize) {
                Transaction newTransaction = accountTransactions.get(accountTransactions.size() - 1);
                transactions.add(newTransaction);
            }
            
            saveDataToFile();
            return true;
        } catch (InsufficientFundsException | IllegalArgumentException e) {
            System.err.println("Withdraw error: " + e.getMessage());
            return false;
        }
    }

    public boolean transfer(String sendingAccountId, String receivingAccountId, double amount) {
        Account sendingAccount = findAccountById(sendingAccountId);
        Account receivingAccount = findAccountById(receivingAccountId);

        if (sendingAccount == null || receivingAccount == null ) return false;

        try {
            int initialSize = sendingAccount.getTransactions().size();
            
            sendingAccount.transfer(receivingAccount, amount);
            
            List<Transaction> sendingAccountTransactions = sendingAccount.getTransactions();
            if (sendingAccountTransactions.size() > initialSize) {
                Transaction newTransaction = sendingAccountTransactions.get(sendingAccountTransactions.size() - 1);
                transactions.add(newTransaction);
            }
            
            saveDataToFile();
            return  true;
        } catch (InsufficientFundsException e) {
           System.err.println("Transfer error: " + e.getMessage());
           return false;
        }
    }

 public List<Transaction> getTransactionsByAccount(String accountId) {
    List<Transaction> sortedTransactions = new ArrayList<>();
    List<String> addedTransactionIds = new ArrayList<>();
    
    for (Transaction transaction : transactions) {
        if (addedTransactionIds.contains(transaction.getId())) {
            continue;
        }
        
        if (transaction.getType().equals("Deposit") || transaction.getType().equals("Withdrawal")) {
            String sendingId = transaction.getSendingAccountId();
            if ((sendingId != null && sendingId.equals(accountId)) || 
                (transaction.getType().equals("Withdrawal") && 
                 transaction.getReceivingAccountId() != null && 
                 transaction.getReceivingAccountId().equals(accountId))) {
                sortedTransactions.add(transaction);
                addedTransactionIds.add(transaction.getId());
            }
        } else if (transaction.getType().equals("Transfer")) {
            String sendingId = transaction.getSendingAccountId();
            String receivingId = transaction.getReceivingAccountId();
            if ((sendingId != null && sendingId.equals(accountId)) || 
                (receivingId != null && receivingId.equals(accountId))) {
                sortedTransactions.add(transaction);
                addedTransactionIds.add(transaction.getId());
            }
        }
    }
    return sortedTransactions;
}

    public List<Transaction> getTransactionByDateRange(Date startDate, Date endDate) {
        List<Transaction> sortedTransaction = new ArrayList<>();
        for (Transaction transaction: transactions) {
            if (transaction.getTimeStamp().after(startDate) && transaction.getTimeStamp().before(endDate)) {
                sortedTransaction.add(transaction);
            }
        }
        return  sortedTransaction;
    }

    public List<Account> getAccountsByCustomerId(String customerId) {
        Customer customer = findCustomerById(customerId);
        if (customer != null) {
            return new ArrayList<>(customer.getAccounts());
        }
        return new ArrayList<>(); 
    }
}