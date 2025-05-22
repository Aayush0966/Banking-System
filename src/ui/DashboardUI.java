package ui;

import controllers.DashboardController;
import model.Account;
import model.Customer;
import services.TransactionService;

import javax.swing.*;
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

        if (customers.isEmpty()) {
            handleEmptyCustomerTable();
        } else {
            displayCustomerData(customers);
        }
        refreshCustomerTable();
    }

    private void loadAccounts() {
        try {
            // Clear existing data
            accountTableModel.setRowCount(0);
    
            // Get fresh data from controller
            List<Customer> customers = controller.getAllCustomers();
            boolean hasAccounts = false;
    
            // Count total accounts first
            int totalAccounts = 0;
            for (Customer customer : customers) {
                totalAccounts += customer.getAccountsCount();
            }
    
            // Populate account table
            for (Customer customer : customers) {
                if (!customer.getAccounts().isEmpty()) {
                    hasAccounts = true;
                    for (Account account : customer.getAccounts()) {
                        accountTableModel.addRow(new Object[]{
                                account.getId(),
                                account.getAccountNum(),
                                customer.getName(),
                                account.getName(),
                                account.getBalance()
                        });
                    }
                }
            }
    
            // Handle UI update based on account presence
            if (totalAccounts == 0) {
                // No accounts, show placeholder
                handleEmptyAccountTable();
            } else {
                // Has accounts, show table
                showAccountTable();
            }
    
            // Always refresh the UI
            SwingUtilities.invokeLater(() -> {
                if (mainPanel != null) {
                    mainPanel.revalidate();
                    mainPanel.repaint();
                }
            });
            
            // Log for debugging
            System.out.println("Loaded " + totalAccounts + " accounts, table has " + accountTableModel.getRowCount() + " rows");
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

        // Add customer using controller
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

        String validationError = controller.validateAccountData(accountName, initialDepositStr);
        if (validationError != null) {
            showErrorMessage(validationError);
            return;
        }

        try {
            double initialDeposit = Double.parseDouble(initialDepositStr);

            if (controller.addAccount(selectedCustomer.getId(), accountName, initialDeposit)) {
                showInfoMessage("Account added successfully");
                
                // First access the tabbed pane to switch to the Accounts tab
                JTabbedPane tabbedPane = (JTabbedPane) mainPanel.getComponent(1);
                tabbedPane.setSelectedIndex(1); // Switch to Accounts tab
                
                // Reload data in the correct order
                loadCustomers();
                
                // Find the table panel
                Container tablePanel = findParentWithName("accountTablePanel");
                if (tablePanel != null) {
                    // Remove any placeholders
                    removeSpecificPlaceholderPanel(tablePanel, "accountPlaceholder");
                }
                
                // Load accounts after placeholders are removed
                loadAccounts();
                
                // Make sure the account table is visible
                accountTable.setVisible(true);
                
                // Refresh UI
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

    // Add a window listener to refresh data when TransactionUI closes
    transactionUI.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
            // Ensure updates happen on the EDT
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
                // Reload customer data first
                loadCustomers();
                
                // Check if this was the last account before reloading accounts
                List<Customer> customers = controller.getAllCustomers();
                boolean hasAnyAccounts = customers.stream().anyMatch(c -> !c.getAccounts().isEmpty());
                
                if (!hasAnyAccounts) {
                    // Find the table panel before refreshing the accounts
                    Container tablePanel = findParentWithName("accountTablePanel");
                    if (tablePanel != null) {
                        // Force removal of any remaining components except the table
                        for (Component c : tablePanel.getComponents()) {
                            if (c != accountTable && c instanceof JPanel) {
                                tablePanel.remove(c);
                            }
                        }
                    }
                }
                
                // Now reload accounts
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

private void handleEmptyCustomerTable() {
    JPanel placeholderPanel = createPlaceholderPanel("No customers available. Add a customer to get started.");
    customerTable.setVisible(false);
    customerTable.getParent().add(placeholderPanel);
}

private void displayCustomerData(List<Customer> customers) {
    customerTable.setVisible(true);

    Container parent = customerTable.getParent();
    if (parent != null) {
        removePlaceholderPanels(parent);
    }

    for (Customer customer : customers) {
        customerTableModel.addRow(new Object[]{
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAccountsCount()
        });
    }
}

private void handleEmptyAccountTable() {
    try {
        // Get the parent panel that contains the table
        Container parent = findParentWithName("accountTablePanel");
        if (parent != null) {
            // First remove any existing placeholder panels
            removeSpecificPlaceholderPanel(parent, "accountPlaceholder");
            
            // Create and add the new placeholder
            JPanel placeholderPanel = createPlaceholderPanel("No accounts available. Add an account to get started.");
            placeholderPanel.setName("accountPlaceholder");
            
            // Hide the table and add the placeholder
            accountTable.setVisible(false);
            parent.add(placeholderPanel);
            parent.revalidate();
            parent.repaint();
        }
    } catch (Exception e) {
        System.err.println("Error handling empty account table: " + e.getMessage());
    }
}

private void showAccountTable() {
    try {
        // Find the correct parent panel
        Container parent = findParentWithName("accountTablePanel");
        if (parent != null) {
            // Remove any placeholder panels first
            removeSpecificPlaceholderPanel(parent, "accountPlaceholder");
            
            // Show the table and refresh the UI
            accountTable.setVisible(true);
            parent.revalidate();
            parent.repaint();
        }
    } catch (Exception e) {
        System.err.println("Error showing account table: " + e.getMessage());
    }
}

private JPanel createPlaceholderPanel(String message) {
    JPanel placeholderPanel = new JPanel();
    placeholderPanel.setBackground(SECONDARY_COLOR);
    placeholderPanel.setLayout(new BorderLayout());
    JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
    messageLabel.setFont(LABEL_FONT);
    messageLabel.setForeground(TEXT_COLOR);
    placeholderPanel.add(messageLabel, BorderLayout.CENTER);
    return placeholderPanel;
}

private void removePlaceholderPanels(Container parent) {
    if (parent == null) {
        return;
    }
    Component[] components = parent.getComponents();
    for (Component c : components) {
        if (c instanceof JPanel && !(c == accountTable || c == customerTable)) {
            parent.remove(c);
        }
    }
}

// Add a more specific method to remove only the placeholder panel by name
private void removeSpecificPlaceholderPanel(Container parent, String name) {
    if (parent == null) {
        return;
    }
    Component[] components = parent.getComponents();
    for (Component c : components) {
        if (c instanceof JPanel && name.equals(c.getName())) {
            parent.remove(c);
        }
    }
}

// Helper method to find a parent container with a specific name
private Container findParentWithName(String name) {
    // Look for the specific table panel in the component hierarchy
    Component comp = accountTable;
    while (comp != null) {
        comp = comp.getParent();
        if (comp instanceof JPanel && name.equals(comp.getName())) {
            return (Container) comp;
        }
        // If we reach the tabbed pane, search its components
        if (comp instanceof JTabbedPane) {
            JTabbedPane tabbedPane = (JTabbedPane) comp;
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                Component tabComp = tabbedPane.getComponentAt(i);
                if (tabComp instanceof Container) {
                    Container result = searchComponentByName((Container) tabComp, name);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
    }
    
    // If we couldn't find it through parent hierarchy, look starting from mainPanel
    if (mainPanel != null) {
        return searchComponentByName(mainPanel, name);
    }
    
    return null;
}

// Helper method to search for a component with a specific name
private Container searchComponentByName(Container container, String name) {
    if (name.equals(container.getName())) {
        return container;
    }
    
    Component[] components = container.getComponents();
    for (Component comp : components) {
        if (name.equals(comp.getName())) {
            return (Container) comp;
        }
        if (comp instanceof Container) {
            Container result = searchComponentByName((Container) comp, name);
            if (result != null) {
                return result;
            }
        }
    }
    
    return null;
}

private void refreshCustomerTable() {
    if (customerTable.getParent() != null) {
        customerTable.getParent().revalidate();
        customerTable.getParent().repaint();
    } else {
        // If parent is not available, refresh the whole panel
        SwingUtilities.invokeLater(() -> {
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }
}

private void refreshAccountTable() {
    if (accountTable.getParent() != null) {
        accountTable.getParent().revalidate();
        accountTable.getParent().repaint();
    } else {
        // If parent is not available, refresh the whole panel
        SwingUtilities.invokeLater(() -> {
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }
}
}