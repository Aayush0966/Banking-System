package ui;

import controllers.DashboardController;
import model.Account;
import model.Customer;
import services.TransactionService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardUI extends BaseFrame {
    private TransactionService transactionService;
    private DashboardController controller;
    private JTable customerTable;
    private DefaultTableModel customerTableModel;
    private JTable accountTable;
    private DefaultTableModel accountTableModel;
    private JPanel customerMessagePanel;
    private JPanel accountMessagePanel;

    public DashboardUI() {
        super("Admin Dashboard");
        init();
        initializeController();
        loadCustomers();
        loadAccounts();
    }

    @Override
    protected void setupUI() {
        setSize(800, 600);
        transactionService = new TransactionService();
        JPanel headerPanel = createHeaderPanel(this::handleLogout);
        customerTableModel = new DefaultTableModel(new Object[]{"Id", "Name", "Email", "Phone", "Accounts"}, 0);
        customerTable = new JTable(customerTableModel);
        accountTableModel = new DefaultTableModel(new Object[]{"Id", "Account Number", "Customer Name", "Account Name", "Balance"}, 0);
        accountTable = new JTable(accountTableModel);
        customerMessagePanel = createMessagePanel("No customers found. Please add a customer.");
        accountMessagePanel = createMessagePanel("No accounts found. Please add an account.");
        JTabbedPane tabbedPanel = createTabbedPane(
                this::handleAddCustomer,
                this::handleEditCustomer,
                this::handleRemoveCustomer,
                this::handleAddAccount,
                this::handleAccountOperations,
                this::handleRemoveAccount,
                customerTableModel,
                customerTable,
                accountTableModel,
                accountTable
        );
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void initializeController() {
        controller = new DashboardController(transactionService);
    }

    private void loadCustomers() {
        customerTableModel.setRowCount(0);
        List<Customer> customers = controller.getAllCustomers();

        JTabbedPane tabbedPane = (JTabbedPane) mainPanel.getComponent(1);
        JPanel customerPanel = (JPanel) tabbedPane.getComponentAt(0);
        
        Component[] components = customerPanel.getComponents();
        JPanel tableContainer = null;
        for (Component comp : components) {
            if (comp instanceof JPanel && comp.getName() != null && comp.getName().equals("customerTablePanel")) {
                tableContainer = (JPanel) comp;
            }
        }
        if (tableContainer == null) {
            tableContainer = (JPanel) customerPanel.getComponent(0);
        }
        tableContainer.removeAll(); 
        if (customers.isEmpty()) {
            tableContainer.add(customerMessagePanel, BorderLayout.CENTER);
        } else {
            for (Customer customer : customers) {
                customerTableModel.addRow(new Object[]{
                        customer.getId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        customer.getAccountsCount()
                });
            }
            
            JScrollPane scrollPane = new JScrollPane(customerTable);
            tableContainer.add(scrollPane, BorderLayout.CENTER);
        }
        refreshCustomerTable();
    }

    private void loadAccounts() {
        try {
            accountTableModel.setRowCount(0);
    
            List<Customer> customers = controller.getAllCustomers();
            
            JTabbedPane tabbedPane = (JTabbedPane) mainPanel.getComponent(1);
            JPanel accountPanel = (JPanel) tabbedPane.getComponentAt(1); // Accounts tab
            
            Component[] components = accountPanel.getComponents();
            JPanel tableContainer = null;
            for (Component comp : components) {
                if (comp instanceof JPanel && comp.getName() != null && comp.getName().equals("accountTablePanel")) {
                    tableContainer = (JPanel) comp;
                    break;
                }
            }
            
            if (tableContainer == null) {
                tableContainer = (JPanel) accountPanel.getComponent(0);
            }
            
            tableContainer.removeAll(); 
            
            boolean hasAccounts = false;
            
            for (Customer customer : customers) {
                if (!customer.getAccounts().isEmpty()) {
                    for (Account account : customer.getAccounts()) {
                        accountTableModel.addRow(new Object[]{
                                account.getId(),
                                account.getAccountNum(),
                                customer.getName(),
                                account.getName(),
                                account.getBalance()
                        });
                        hasAccounts = true;
                    }
                }
            }
            
            if (!hasAccounts) {
                tableContainer.add(accountMessagePanel, BorderLayout.CENTER);
            } else {
                JScrollPane scrollPane = new JScrollPane(accountTable);
                tableContainer.add(scrollPane, BorderLayout.CENTER);
            }
            
            SwingUtilities.invokeLater(() -> {
                if (mainPanel != null) {
                    mainPanel.revalidate();
                    mainPanel.repaint();
                }
            });
            
            System.out.println("Loaded accounts, table has " + accountTableModel.getRowCount() + " rows");
        } catch (Exception e) {
            System.err.println("Error loading accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }
private void handleAddCustomer() {
    JTextField nameField = new JTextField(15);
    JTextField emailField = new JTextField(15);
    JTextField phoneField = new JTextField(15);

    JPanel panel = createFormPanel(
            new JLabel("Name: "), nameField,
            new JLabel("Email: "), emailField,
            new JLabel("Phone: "), phoneField
    );
    panel.setForeground(Color.BLACK);

    int result = JOptionPane.showConfirmDialog(this, panel, "Add customer",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        String validationError = controller.validateCustomerData(name, email, phone);
        if (validationError != null) {
            showErrorMessage(validationError);
            return;
        }

        if (controller.addCustomer(name, email, phone)) {
            showInfoMessage("Customer added successfully");
            loadCustomers();
        } else {
            showErrorMessage("Failed to add customer");
        }
    }
}

private void handleEditCustomer() {
    int selectedRow = customerTable.getSelectedRow();

    if (selectedRow == -1) {
        showErrorMessage("Please select a customer to edit");
        return;
    }

    String customerId = customerTable.getValueAt(selectedRow, 0).toString();
    Customer customer = controller.findCustomerById(customerId);

    if (customer == null) {
        showErrorMessage("Customer not found");
        return;
    }

    JTextField nameField = new JTextField(customer.getName(), 15);
    JTextField emailField = new JTextField(customer.getEmail(), 15);
    JTextField phoneField = new JTextField(customer.getPhone(), 15);

    JPanel panel = createFormPanel(
            new JLabel("Name:"), nameField,
            new JLabel("Email:"), emailField,
            new JLabel("Phone:"), phoneField
    );

    int result = JOptionPane.showConfirmDialog(this, panel, "Edit Customer",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        String validationError = controller.validateCustomerData(name, email, phone);
        if (validationError != null) {
            showErrorMessage(validationError);
            return;
        }

        if (controller.updateCustomer(customerId, name, email, phone)) {
            showInfoMessage("Customer updated successfully");
            loadCustomers();
        } else {
            showErrorMessage("Failed to update customer");
        }
    }
}

private void handleRemoveCustomer() {
    int selectedRow = customerTable.getSelectedRow();
    if (selectedRow == -1) {
        showErrorMessage("Please select a customer to remove");
        return;
    }

    String customerId = customerTable.getValueAt(selectedRow, 0).toString();

    if (!controller.canRemoveCustomer(customerId)) {
        showErrorMessage("Unable to remove customer. Please close all active accounts first.");
        return;
    }

    int confirm = showConfirmDialog("Do you want to remove this customer?");
    if (confirm == JOptionPane.YES_OPTION) {
        if (controller.removeCustomer(customerId)) {
            showInfoMessage("Customer removed successfully");
            loadCustomers();
        } else {
            showErrorMessage("Failed to remove customer");
        }
    }
}

private void handleAddAccount() {
    List<Customer> customers = controller.getAllCustomers();

    if (customers.isEmpty()) {
        showErrorMessage("No customers found. Please add a customer first");
        return;
    }

    String[] accountTypes = {"Savings", "Checking", "Money Market", "Fixed Deposit"};
    JComboBox<Customer> customerCombo = createDropdown(customers);
    JComboBox<String> accountTypeCombo = createDropdown(List.of(accountTypes));
    JTextField initialDepositField = new JTextField("0.0", 20);
    JPanel panel = createFormPanel(
            new JLabel("Customer: "), customerCombo,
            new JLabel("Account Type: "), accountTypeCombo,
            new JLabel("Initial Deposit: "), initialDepositField
    );

    int result = JOptionPane.showConfirmDialog(this, panel, "Add Account", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
        if (selectedCustomer == null) {
            showErrorMessage("Please select a customer");
            return;
        }

        String accountName = (String) accountTypeCombo.getSelectedItem();
        String initialDepositStr = initialDepositField.getText().trim();

        if (initialDepositStr.indexOf('.') != initialDepositStr.lastIndexOf('.')) {
            showErrorMessage("Invalid amount format. Amount cannot contain multiple decimal points");
            return;
        }

        String validationError = controller.validateAccountData(accountName, initialDepositStr);
        if (validationError != null) {
            showErrorMessage(validationError);
            return;
        }
        try {
            double initialDeposit = Double.parseDouble(initialDepositStr);
            if (controller.addAccount(selectedCustomer.getId(), accountName, initialDeposit)) {
                showInfoMessage("Account added successfully");
                JTabbedPane tabbedPane = (JTabbedPane) mainPanel.getComponent(1);
                tabbedPane.setSelectedIndex(1); 
                loadCustomers();
                loadAccounts();
                SwingUtilities.invokeLater(() -> {
                    mainPanel.revalidate();
                    mainPanel.repaint();
                });
            } else {
                showErrorMessage("Failed to add account");
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid deposit amount");
        }
    }
}

private void handleAccountOperations() {
    int selectedRow = accountTable.getSelectedRow();
    if (selectedRow == -1) {
        showErrorMessage("Please select an account to manage");
        return;
    }
    String accountId = accountTable.getValueAt(selectedRow, 0).toString();
    Account account = controller.findAccountById(accountId);
    if (account == null) {
        showErrorMessage("Account not found");
        return;
    }
    Customer customer = controller.findCustomerById(account.getCustomerId());
    if (customer == null) {
        showErrorMessage("Customer not found");
        return;
    }
    TransactionUI transactionUI = new TransactionUI(this, transactionService, account, customer);
    transactionUI.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
            SwingUtilities.invokeLater(() -> {
                loadCustomers();
                loadAccounts();
                refreshCustomerTable();
                refreshAccountTable();
            });
        }
    });
}

private void handleRemoveAccount() {
    int selectedRow = accountTable.getSelectedRow();
    if (selectedRow == -1) {
        showErrorMessage("Please select an account to remove");
        return;
    }
    String accountId = accountTable.getValueAt(selectedRow, 0).toString();
    Account account = controller.findAccountById(accountId);
    if (account == null) {
        showErrorMessage("Account not found");
        return;
    }
    int confirm = showConfirmDialog("Are you sure you want to remove this account?");
    if (confirm == JOptionPane.YES_OPTION) {
        if (controller.removeAccount(accountId)) {
            showInfoMessage("Account removed successfully");
            try {
                
                loadCustomers();
                loadAccounts();
            } catch (Exception e) {
                System.err.println("Error handling account removal: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showErrorMessage("Failed to remove account");
        }
    }
}

private void handleLogout() {
    dispose();
}



private void refreshCustomerTable() {
    JTabbedPane tabbedPane = (JTabbedPane) mainPanel.getComponent(1);
    JPanel customerPanel = (JPanel) tabbedPane.getComponentAt(0); 
    
    SwingUtilities.invokeLater(() -> {
        customerPanel.revalidate();
        customerPanel.repaint();
    });
}

private void refreshAccountTable() {
    JTabbedPane tabbedPane = (JTabbedPane) mainPanel.getComponent(1);
    JPanel accountPanel = (JPanel) tabbedPane.getComponentAt(1);
    
    SwingUtilities.invokeLater(() -> {
        accountPanel.revalidate();
        accountPanel.repaint();
    });
}

private JPanel createMessagePanel(String message) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(SECONDARY_COLOR);
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));
    
    JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
    messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    messageLabel.setForeground(TEXT_COLOR);
    
    panel.add(messageLabel, BorderLayout.CENTER);
    return panel;
}
}