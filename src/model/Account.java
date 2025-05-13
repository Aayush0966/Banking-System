package model;

import exceptions.InsufficientFundsException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Account extends BankEntity {
    private String accountNumber;
    private String customerId;
    private List<Transaction> transactions;
    private double balance;

    public Account(String name,  String accountNumber, String customerId) {
        super(name);
        this.transactions =  new ArrayList<>();
        this.accountNumber = accountNumber;
        this.balance = 0.0;
        this.customerId = customerId;
    }

    public String getAccountNum() {
        return accountNumber;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
        Transaction transaction = new Transaction("Deposit", amount, this.id, null, new Date());
        transactions.add(transaction);
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > this.balance) {
            throw new InsufficientFundsException("Insufficient funds. Current balance: " + balance);
        }
        balance -= amount;
        Transaction transaction = new Transaction("Withdrawal", amount, null, this.id, new Date());
        transactions.add(transaction);
    }

    public void transfer(Account receiver, double amount) throws  InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Transfer amount must be positive");
        if (balance < amount) throw new InsufficientFundsException("Insufficient funds. Current balance: " + balance);
        balance -= amount;
        receiver.balance += amount;
        Transaction transaction = new Transaction("Transfer", amount, this.id, receiver.id, new Date());
        transactions.add(transaction);
        receiver.transactions.add(transaction);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}