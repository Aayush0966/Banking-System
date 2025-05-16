package ui;

import model.Account;
import model.Customer;
import model.Transaction;
import services.TransactionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class TransactionUI extends BaseFrame {
    private final TransactionService transactionService;
    private Account account;
    private final Customer customer;
    private final JTable transactionTable;
    private final DefaultTableModel transactionTableModel;
    private final JLabel balanceLabel;

    public TransactionUI(JFrame parent, TransactionService transactionService, Account account, Customer customer) {
        super("Account Management - " + account.getName());
        this.transactionService = transactionService;
        this.account = account;
        this.customer = customer;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transactionTableModel = new DefaultTableModel(
                new Object[]{"ID", "Type", "Amount", "Date", "From/To"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(transactionTableModel);
        balanceLabel = createLabel("Balance: $" + account.getBalance(), 14, Font.BOLD);

        setupUI();
        loadTransactions();

        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public void setupUI() {
        setSize(700, 500);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel accountInfoLabel = createLabel(
                "Account: " + account.getName() + " (" + account.getAccountNum() + ")" +
                        " | Customer: " + customer.getName(), 12, Font.PLAIN);
        headerPanel.add(accountInfoLabel, BorderLayout.WEST);
        headerPanel.add(balanceLabel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        mainPanel.add(createTablePanel(transactionTableModel, transactionTable), BorderLayout.CENTER);

        String[] labels = {"Deposit", "Withdraw", "Transfer", "Close"};
        Runnable[] actions = {
                this::handleDeposit,
                this::handleWithdraw,
                this::handleTransfer,
                this::dispose
        };
        mainPanel.add(createButtonPanel(labels, actions), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadTransactions() {
        transactionTableModel.setRowCount(0);
        List<Transaction> transactions = transactionService.getTransactionsByAccount(account.getId());
        System.out.println("Loading transactions..." + transactions.size());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Transaction transaction : transactions) {
            String fromTo = switch (transaction.getType()) {
                case "Deposit" -> "External → " + account.getAccountNum();
                case "Withdrawal" -> account.getAccountNum() + " → External";
                case "Transfer" -> {
                    if (account.getId().equals(transaction.getSendingAccountId())) {
                        Account target = transactionService.findAccountById(transaction.getReceivingAccountId());
                        yield account.getAccountNum() + " → " + (target != null ? target.getAccountNum() : "Unknown");
                    } else {
                        Account source = transactionService.findAccountById(transaction.getSendingAccountId());
                        yield (source != null ? source.getAccountNum() : "Unknown") + " → " + account.getAccountNum();
                    }
                }
                default -> "";
            };

            transactionTableModel.addRow(new Object[]{
                    transaction.getId(),
                    transaction.getType(),
                    transaction.getAmount(),
                    dateFormat.format(transaction.getTimeStamp()),
                    fromTo
            });
        }

        account = transactionService.findAccountById(account.getId()); // Refresh
        balanceLabel.setText("Balance: $" + account.getBalance());
    }

    private void handleDeposit() {
        String input = showInputDialog("Enter deposit amount:");
        handleAmountTransaction(input, "Deposit", amount -> transactionService.deposit(account.getId(), amount));
    }

    private void handleWithdraw() {
        String input = showInputDialog("Enter withdrawal amount:");
        handleAmountTransaction(input, "Withdrawal", amount -> transactionService.withdraw(account.getId(), amount));
    }

    private void handleTransfer() {
        String targetIdStr = showInputDialog("Enter target account ID:");
        if (targetIdStr == null || targetIdStr.isEmpty()) return;

        String amountStr = showInputDialog("Enter amount to transfer:");
        if (amountStr == null || amountStr.isEmpty()) return;

        try {
            long targetId = Long.parseLong(targetIdStr);
            double amount = Double.parseDouble(amountStr);

            if (amount <= 0) {
                showErrorMessage("Amount must be positive.");
                return;
            }

            if (transactionService.transfer(account.getId(), String.valueOf(targetId), amount)) {
                showInfoMessage("Transfer successful");
                loadTransactions();
            } else {
                showErrorMessage("Transfer failed. Check details and balance.");
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter valid numeric values.");
        }
    }

    private void handleAmountTransaction(String input, String type, java.util.function.Function<Double, Boolean> action) {
        if (input == null || input.isEmpty()) return;

        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                showErrorMessage("Amount must be positive.");
                return;
            }

            if (action.apply(amount)) {
                showInfoMessage(type + " successful");
                loadTransactions();
                List<Transaction> transactions = transactionService.getTransactionsByAccount(account.getId());
            } else {
                showErrorMessage("Failed to process " + type.toLowerCase() + ".");
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter a valid number.");
        }
    }
}