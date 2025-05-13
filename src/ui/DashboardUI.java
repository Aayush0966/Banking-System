package ui;

import interfaces.IAuthService;
import model.Account;
import model.Customer;
import services.TransactionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class DashboardUI extends BaseFrame {
    private final IAuthService authService;
    private TransactionService transactionService;
    private JTable customerTable;
    private DefaultTableModel customerTableModel;
    private JTable accountTable;
    private DefaultTableModel accountTableModel;


    public DashboardUI(IAuthService authService) {
        super("Admin Dashboard");
        init();
        this.authService = authService;
        loadCustomers();
        loadAccounts();

    }

    @Override
    protected void setupUI() {
        setSize(800, 600);
        transactionService = new TransactionService();
        JPanel headerPanel = createHeaderPanel("Admin Dashboard", this::handleLogout );
        customerTableModel = new DefaultTableModel(new Object[]{"Id", "Name", "Email", "Phone", "Accounts"}, 0);
        customerTable = new JTable();
        accountTableModel = new DefaultTableModel(new Object[]{"Id", "Account Number", "Customer Name", "Account Name", "Balance"}, 0);
        accountTable = new JTable();
        JTabbedPane tabbedPanel = createTabbedPane(
                this::handleAddCustomer,
                this::handleEditCustomer,
                this::handleRemoveCustomer,
                this::handleAddAccount,
                this::handleEditAccount,
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

    private void loadCustomers() {
        customerTableModel.setRowCount(0);
        for (Customer customer: transactionService.getAllCustomers()) {
            customerTableModel.addRow(new Object[]{
                    customer.getId(),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getPhone(),
                    customer.getAccounts() != null ? customer.getAccounts().toString() : ""
            });

        }
    }

    protected void handleAddCustomer() {
        JTextField nameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JTextField phoneField = new JTextField(15);

        JPanel panel = createFormPanel(
                new JLabel("Name: "), nameField,
                new JLabel("Email: "), emailField,
                new JLabel("Phone: "), phoneField
        );

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Customer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                showErrorMessage("Name and Email are required");
                return;
            }

            Customer customer = new Customer(name, email, phone);

            if (transactionService.addCustomer(customer)) {
                showInfoMessage("Customer added");
                loadCustomers();
            }
            else {
                showErrorMessage("Customer not added");
            }
        }

    }

    protected void handleEditCustomer() {
        int selectedRow = customerTable.getSelectedRow();

        if (selectedRow == -1) {
            showErrorMessage("Please select a customer to edit");
            return;
        }

        String customerId = customerTable.getValueAt(selectedRow, 0).toString();
        Customer customer = transactionService.findCustomerById(customerId);

        if (customer == null) {
            showErrorMessage("Customer not found");
            return;
        }

        JTextField nameField = new JTextField(customer.getName());
        JTextField emailField = new JTextField(customer.getEmail());
        JTextField phoneField = new JTextField(customer.getPhone());
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

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                showErrorMessage("All fields are required");
            }

            customer.setName(name);
            customer.setEmail(email);
            customer.setPhone(phone);

            if (transactionService.updateCustomer(customer)) {
                showInfoMessage("Customer updated");
                loadCustomers();

            }
            else {
                showErrorMessage("Customer not updated");
            }

        }
    }

    protected void handleRemoveCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Please select a customer to remove");
            return;
        }

        String customerId = customerTable.getValueAt(selectedRow, 0).toString();
        Customer customer = transactionService.findCustomerById(customerId);
        if (customer == null) {
            showErrorMessage("Customer not found");
            return;
        }

        if (!customer.getAccounts().isEmpty()) {
            showErrorMessage("You cannot remove customer with active accounts");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, customer, "Remove Customer", JOptionPane.OK_CANCEL_OPTION);
        if (confirm == JOptionPane.OK_OPTION) {
            if (transactionService.deleteCustomer(customerId)) {
                showInfoMessage("Customer removed");
                loadCustomers();
            }
            else {
                showErrorMessage("Customer not removed");
            }
        }

    }

    protected void handleAddAccount() {
        List<Customer> customers = transactionService.getAllCustomers();

        if (customers.isEmpty()) {
            showErrorMessage("No customers found. Please add a customer first");
            return;
        }
        JComboBox<Customer> customerCombo = createCustomerDropdown(customers);

        JTextField accountNameField = new JTextField();
        JTextField initialDepositField = new JTextField("0.0");

        JPanel panel = createFormPanel(
                new JLabel("Customer: "), customerCombo,
                new JLabel("Account Name: "), accountNameField,
                new JLabel("Initial Deposit: "), initialDepositField
        );

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Account", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
            if (selectedCustomer == null) {
                showErrorMessage("Customer not found");
                return;
            }
            String accountName = accountNameField.getText().trim();
            String initialDepositStr = initialDepositField.getText().trim();

            if (accountName.isEmpty() || initialDepositStr.isEmpty()) {

                showErrorMessage("All fields are required");
                return;
            }

            try {
                double initialDeposit = Double.parseDouble(initialDepositStr);
                if (initialDeposit < 0) {
                    showErrorMessage("Initial deposit cannot be negative");
                    return;
                }

                String accountNumber = generateAccountNumber();
                Account account = new Account(accountName, accountNumber, selectedCustomer.getId());

                if (transactionService.addAccount(selectedCustomer.getId(), account)) {
                    if (initialDeposit > 0) {
                        boolean balanceAdded = transactionService.deposit(account.getId(), initialDeposit);
                    }
                    showInfoMessage("Account added");
                    loadCustomers();
                    loadAccounts();
                }
                else {
                    showErrorMessage("Account not added");
                }

            } catch (NumberFormatException e) {
                showErrorMessage("Invalid deposit value");
            }
        }

    }

    protected void loadAccounts() {
        accountTableModel.setRowCount(0);

        List<Customer> customers = transactionService.getAllCustomers();
        for (Customer customer : customers) {
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

    protected String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }

    protected void handleEditAccount() {
        int selectedRow = accountTable.getSelectedRow();

        if (selectedRow == -1) {
            showErrorMessage("Please select a customer to edit");
            return;
        }
        String accountId = accountTable.getValueAt(selectedRow, 0).toString();
        Account account = transactionService.findAccountById(accountId);
        if (account == null) {
            showErrorMessage("Account not found");
            return;
        }

        Customer editedCustomer = transactionService.findCustomerById(account.getCustomerId());
        if (editedCustomer == null) {
            showErrorMessage("Customer not found");
            return;
        }

        new TransactionUI(this, transactionService, account, editedCustomer);


    }

    void handleLogout() {
        authService.logout();
        dispose();
    }
}