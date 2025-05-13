package ui;

import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.awt.*;
public abstract class BaseFrame extends JFrame {
    protected JPanel mainPanel;

    public BaseFrame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public final void init() {
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
            button.addActionListener(e -> actions[finalIndex].run());
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

    protected JLabel createLabel(String text, int fontSize, int style) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, fontSize));
        return label;
    }

    protected JPanel createTablePanel(DefaultTableModel model, JTable table) {
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
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
            Runnable handleManageAccounts,
            DefaultTableModel customerTableModel,
            JTable customerTable,
            DefaultTableModel accountTableModel,
            JTable accountTable

    ) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Customers", createCustomerPanel(
            handleAddCustomer, customerTableModel, customerTable, handleEditCustomer, handleRemoveCustomer
        ));
        tabbedPane.addTab("Accounts", createAccountsPanel(
                    accountTableModel, accountTable, handleAddAccounts,handleManageAccounts
        ));
        return tabbedPane;
    }

    protected JPanel createCustomerPanel(Runnable handleAddCustomer, DefaultTableModel model, JTable table, Runnable handleRemoveCustomer, Runnable handleEditCustomer) {
        JPanel customerPanel = new JPanel(new BorderLayout());
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        customerPanel.add(scrollPane, BorderLayout.CENTER);
        String[] labels = {"Add Customer", "Edit Customer", "Remove Customer"};
        Runnable[] actions = {handleAddCustomer, handleRemoveCustomer, handleEditCustomer};
        JPanel buttonPanel = createButtonPanel(labels, actions);
        customerPanel.add(buttonPanel, BorderLayout.SOUTH);
        return customerPanel;
    }

    protected JPanel createAccountsPanel( DefaultTableModel accountTableModel, JTable accountTable, Runnable handleAddAccount, Runnable handleManageAccount) {
        JPanel accountPanel = new JPanel(new BorderLayout());
        accountTable.setModel(accountTableModel);
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

    protected JComboBox<Customer> createCustomerDropdown(List<Customer> customers) {
        JComboBox<Customer> comboBox = new JComboBox<>();

        for (Customer customer : customers) {
            comboBox.addItem(customer);
        }

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Customer) {
                    setText(((Customer) value).getName());
                }
                return this;
            }
        });

        return comboBox;
    }
}