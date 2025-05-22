package ui;

import controllers.TransactionController;
import model.Account;
import model.Customer;
import model.Transaction;
import services.TransactionService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionUI extends BaseFrame {
    private final TransactionService transactionService;
    private TransactionController controller;
    private final JTable transactionTable;
    private final DefaultTableModel transactionTableModel;
    private final JLabel balanceLabel;
    private final JLabel accountInfoLabel;

    public TransactionUI(JFrame parent, TransactionService transactionService, Account account, Customer customer) {
        super("Account Management - " + account.getName());
        this.transactionService = transactionService;

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
        accountInfoLabel = createLabel("", 12, Font.PLAIN);
        initializeController(account.getId());
        setupUI();
        loadTransactions();
        updateAccountInfo();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void initializeController(String accountId) {
        controller = new TransactionController(transactionService, accountId);
    }

    @Override
    protected void setupUI() {
        setSize(700, 500);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(createTablePanel(transactionTableModel, transactionTable), BorderLayout.CENTER);
        JPanel buttonPanel = createActionButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(accountInfoLabel, BorderLayout.WEST);
        headerPanel.add(balanceLabel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createActionButtonPanel() {
        String[] labels = {"Deposit", "Withdraw", "Transfer", "Close"};
        Runnable[] actions = {
                this::handleDeposit,
                this::handleWithdraw,
                this::handleTransfer,
                this::handleClose
        };
        return createButtonPanel(labels, actions);
    }

    private void loadTransactions() {
        transactionTableModel.setRowCount(0);
        List<Transaction> transactions = controller.getTransactions();
        System.out.println("Loading transactions..." + transactions.size());
        for (Transaction transaction : transactions) {
            Object[] rowData = controller.formatTransactionForDisplay(transaction);
            transactionTableModel.addRow(rowData);
        }
        updateAccountInfo();
    }

    private void updateAccountInfo() {
        accountInfoLabel.setText(controller.getAccountDisplayInfo());
        balanceLabel.setText(controller.getBalanceDisplayText());
    }

    private void handleDeposit() {
        String input = showInputDialog("Enter deposit amount:");
        if (input == null || input.trim().isEmpty()) return;

        String validationError = controller.validateAmount(input);
        if (validationError != null) {
            showErrorMessage(validationError);
            return;
        }
        try {
            double amount = controller.parseAmount(input);
            if (controller.deposit(amount)) {
                showInfoMessage("Deposit successful");
                loadTransactions();
            } else {
                showErrorMessage("Failed to process deposit");
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter a valid number");
        }
    }

    private void handleWithdraw() {
        String input = showInputDialog("Enter withdrawal amount:");
        if (input == null || input.trim().isEmpty()) return;
        String validationError = controller.validateAmount(input);
        if (validationError != null) {
            showErrorMessage(validationError);
            return;
        }
        try {
            double amount = controller.parseAmount(input);

            if (controller.withdraw(amount)) {
                showInfoMessage("Withdrawal successful");
                loadTransactions();
            } else {
                showErrorMessage("Failed to process withdrawal. Check your balance.");
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter a valid number");
        }
    }

    private void handleTransfer() {
        String currentAccountId = controller.getCurrentAccountId();
        Account currentAccount = controller.getCurrentAccount();
        List<Customer> allCustomers = controller.getAllCustomers();
        List<Customer> otherCustomers = new ArrayList<>();
        Customer currentAccountOwner = controller.getAccountOwner();
        String currentCustomerId = currentAccountOwner != null ? currentAccountOwner.getId() : "";
        for (Customer customer : allCustomers) {
            if (currentAccountOwner == null || !customer.getId().equals(currentCustomerId)) {
                otherCustomers.add(customer);
            }
        }
        if (otherCustomers.isEmpty()) {
            showErrorMessage("No other customers found. Please add other customers first");
            return;
        }
        JComboBox<Customer> customerCombo = createDropdown(otherCustomers);
        JTextField amountField = new JTextField("0.0", 15);
        JComboBox<Account> accountCombo = new JComboBox<>();
        accountCombo.setEnabled(false);

        customerCombo.addActionListener(e -> {
            Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
            if (selectedCustomer != null) {
                List<Account> customerAccounts = controller.getAccountsByCustomerId(selectedCustomer.getId());
                List<Account> availableAccounts = new ArrayList<>();
                for (Account account : customerAccounts) {
                    if (!account.getId().equals(currentAccountId)) {
                        availableAccounts.add(account);
                    }
                }
                accountCombo.removeAllItems();
                if (availableAccounts.isEmpty()) {
                    accountCombo.setEnabled(false);
                    showErrorMessage("Selected customer has no available accounts for transfer");
                } else {
                    for (Account account : availableAccounts) {
                        accountCombo.addItem(account);
                    }
                    accountCombo.setEnabled(true);
                    accountCombo.setRenderer(new DefaultListCellRenderer() {
                        @Override
                        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                                     int index, boolean isSelected, boolean cellHasFocus) {
                            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                            
                            if (value instanceof Account) {
                                Account account = (Account) value;
                                setText(account.getName());
                            }
                            
                            if (isSelected) {
                                setBackground(ACCENT_COLOR);
                                setForeground(Color.WHITE);
                            } else {
                                setBackground(Color.WHITE);
                                setForeground(TEXT_COLOR);
                            }
                            
                            return this;
                        }
                    });
                }
            } else {
                accountCombo.removeAllItems();
                accountCombo.setEnabled(false);
            }
        });
        if (customerCombo.getItemCount() > 0) {
            customerCombo.setSelectedIndex(0);
        }
        
        JPanel panel = createFormPanel(
                new JLabel("Transfer to Customer: "), customerCombo,
                new JLabel("Transfer to Account: "), accountCombo,
                new JLabel("Amount to Transfer: "), amountField
        );
        int result = JOptionPane.showConfirmDialog(this, panel, "Transfer Money", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
            Account selectedAccount = (Account) accountCombo.getSelectedItem();

            if (selectedCustomer == null) {
                showErrorMessage("Please select a customer");
                return;
            }
            if (selectedAccount == null) {
                showErrorMessage("Please select an account");
                return;
            }
            String amountStr = amountField.getText().trim();
            String validationError = controller.validateTransfer(String.valueOf(selectedAccount.getId()), amountStr);
            if (validationError != null) {
                showErrorMessage(validationError);
                return;
            }

            try {
                double amount = controller.parseAmount(amountStr);
                String targetAccountId = selectedAccount.getId();
                if (controller.transfer(targetAccountId, amount)) {
                    showInfoMessage("Transfer successful");
                    loadTransactions();
                } else {
                    showErrorMessage("Transfer failed. Check details and balance.");
                }
            } catch (NumberFormatException e) {
                showErrorMessage("Please enter valid numeric values");
                e.printStackTrace();
            } catch (Exception e) {
                showErrorMessage("Error during transfer: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleClose() {
        dispose();
    }

    public void refreshTransactionData() {
        loadTransactions();
    }

    public void refreshAccountInfo() {
        updateAccountInfo();
    }
    public JTable getTransactionTable() {
        return transactionTable;
    }

    public DefaultTableModel getTransactionTableModel() {
        return transactionTableModel;
    }

    public JLabel getBalanceLabel() {
        return balanceLabel;
    }
}