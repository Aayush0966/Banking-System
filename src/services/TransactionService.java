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
    private final FileHandler<Customer> customerFileHandler;
    private final FileHandler<Transaction> transactionFileHandler;
    private List<Customer> customers;
    private List<Transaction> transactions;

    public TransactionService() {
        customerFileHandler = new FileHandler<>("customers.ser");
        transactionFileHandler = new FileHandler<>("transactions.ser");
        loadDataFromFile();
    }


    private void loadDataFromFile() {
        try {
            customers = customerFileHandler.loadData();
            transactions = transactionFileHandler.loadData();
        } catch (FileReadException | InvalidDataException e) {
            System.err.println("Error loading data: " + e.getMessage());
            customers = new ArrayList<>();
            transactions = new ArrayList<>();
        }
    }

    private void saveDataToFile() {
        try {
            customerFileHandler.saveData(customers);
            transactionFileHandler.saveData(transactions);
        } catch (FileReadException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
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
            saveDataToFile();
            return true;
        }
        return false;
    }

    public boolean addAccount(String customerId, Account account) {
        Customer customer = findCustomerById(customerId);
        if (customer != null) {
            customer.addAccount(account);
            saveDataToFile();
            return true;
        }
        return false;
    }

    public boolean deleteAccount(String customerId, String accountId) {
        Customer customer = findCustomerById(customerId);
        if (customer == null) return false;
        Account account = findAccountById(accountId);
        if (account == null) return false;
        customer.removeAccount(account);
        saveDataToFile();
        return true;
    }

    public boolean deposit(String accountId, double amount) {
        Account account = findAccountById(accountId);
        if (account == null) return false;
        try {
            account.deposit(amount);
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
            account.withdraw(amount);
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
            sendingAccount.transfer(receivingAccount, amount);
            saveDataToFile();
            return  true;
        } catch (InsufficientFundsException e) {
           System.err.println("Transfer error: " + e.getMessage());
           return false;
        }
    }

    public List<Transaction> getTransactionsByAccount(String accountId) {
        List<Transaction> sortedTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getSendingAccountId().equals(accountId) ||
                    transaction.getReceivingAccountId().equals(accountId)) {
                sortedTransactions.add(transaction);
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


}