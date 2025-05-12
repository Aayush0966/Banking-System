package ui;

import com.sun.source.tree.ReturnTree;
import interfaces.IAuthService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public abstract class BaseFrame extends JFrame {
    protected JPanel mainPanel;

    public BaseFrame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUI();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    protected abstract void setupUI();
    protected JPanel createButtonPanel(String[] labels, Runnable[] actions) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        for (int i = 0; i < labels.length; i++) {
            JButton button = new JButton(labels[i]);
            buttonPanel.add(button);
            int finalIndex = i;
            button.addActionListener(e -> actions[Integer.parseInt(labels[finalIndex])].run());
        }
        return buttonPanel;
    }
    protected JPanel createFormPanel(Component... components) {
        JPanel formPanel = new JPanel(new GridLayout(0,2,10, 10));
        for (Component component : components) {
            formPanel.add(component);
        }
        return formPanel;
    }

    protected JPanel createHeaderPanel(String title, Runnable handleLogout) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout.run());
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);
//        headerPanel.add(searchPanel, BorderLayout.SOUTH);
        return headerPanel;
    }

//    protected JPanel createSearchPanel(JTextField searchField, Action searchAction) {
//        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        searchPanel.add(new JLabel("Search:"));
//        if (!searchField.getText().isEmpty()) {
//            searchPanel.add(searchField);
//        }
//        JButton searchButton = new JButton("Search");
//        searchButton.addActionListener(searchAction);
//        searchPanel.add(searchButton);
//        return searchPanel;
//    }
    protected JTabbedPane createTabbedPane(
            Runnable handleAddCustomer,
            Runnable handleEditCustomer,
            Runnable handleRemoveCustomer,
            Runnable handleAddAccounts,
            Runnable handleManageAccounts
    ) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Customers", createCustomerPanel(
            handleAddCustomer,handleEditCustomer, handleRemoveCustomer
        ));
        tabbedPane.addTab("Accounts", createAccountsPanel(
                    handleAddAccounts,handleManageAccounts
        ));
        return tabbedPane;
    }

    protected JPanel createCustomerPanel(Runnable handleAddCustomer, Runnable handleRemoveCustomer, Runnable handleEditCustomer) {
        JPanel customerPanel = new JPanel(new BorderLayout());
        DefaultTableModel customerTableModel = new DefaultTableModel(new Object[]{"Id", "Name", "Email", "Phone", "Accounts"}, 0);
        JTable customerTable = new JTable(customerTableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        customerPanel.add(scrollPane, BorderLayout.CENTER);
        String[] labels = {"Add Customer", "Edit Customer", "Remove Customer"};
        Runnable[] actions = {handleAddCustomer, handleRemoveCustomer, handleEditCustomer};
        JPanel buttonPanel = createButtonPanel(labels, actions);
        customerPanel.add(buttonPanel, BorderLayout.SOUTH);
        return customerPanel;
    }

    protected JPanel createAccountsPanel(Runnable handleAddAccount, Runnable handleManageAccount) {
        JPanel accountPanel = new JPanel(new BorderLayout());
        DefaultTableModel accountTableModel = new DefaultTableModel(new Object[]{"Id", "Account Number", "Customer Name", "Account Name", "Balance"}, 0);
        JTable accountTable = new JTable(accountTableModel);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(accountTable);
        accountPanel.add(scrollPane, BorderLayout.CENTER);
        String[] labels = {"Add Account", "Manage Account"};
        Runnable[] actions = {handleAddAccount, handleManageAccount };
        JPanel buttonPanel = createButtonPanel(labels, actions);
        accountPanel.add(buttonPanel, BorderLayout.SOUTH);
        return accountPanel;
    }

    protected void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    protected void showErrorMessage(String message) {
        showMessage(message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void showInfoMessage(String message) {
        showMessage(message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void showWarningMessage(String message) {
        showMessage(message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    protected String showInputDialog(String message) {
        return JOptionPane.showInputDialog(this, message);
    }
    protected int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirm", JOptionPane.YES_NO_OPTION);
    }
}
