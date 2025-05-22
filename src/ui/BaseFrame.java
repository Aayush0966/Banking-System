package ui;

import model.Account;
import model.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.awt.*;
public abstract class BaseFrame extends JFrame {
    protected JPanel mainPanel;
    protected static final Color PRIMARY_COLOR = new Color(57, 84, 163);
    protected static final Color SECONDARY_COLOR = new Color(241, 246, 255);
    protected static final Color ACCENT_COLOR = new Color(93, 174, 164);
    protected static final Color TEXT_COLOR = new Color(33, 37, 41);
    protected static final Color ERROR_COLOR = new Color(220, 53, 69);
    protected static final Color SUCCESS_COLOR = new Color(40, 167, 69);

    protected static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
    protected static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    protected static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    protected static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    protected static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public BaseFrame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public final void init() {
        setupUI();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        UIManager.put("Panel.background", SECONDARY_COLOR);
        UIManager.put("Button.background", PRIMARY_COLOR);
        UIManager.put("Button.foreground", TEXT_COLOR);
        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", TEXT_COLOR);
        UIManager.put("PasswordField.background", Color.WHITE);
        UIManager.put("Table.gridColor", new Color(218, 226, 235));
        setMinimumSize(new Dimension(800, 600));
    }

    protected abstract void setupUI();
    protected JPanel createButtonPanel(String[] labels, Runnable[] actions) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(SECONDARY_COLOR);
        for (int i = 0; i < labels.length; i++) {
            int finalI = i;
            JButton button = createButton(labels[finalI], PRIMARY_COLOR, ACCENT_COLOR, e -> actions[finalI].run());
            buttonPanel.add(button);
        }
        return buttonPanel;
    }

    protected JButton createButton(String text, Color bgColor, Color hoverColor, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(12, 15, 12, 15));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

    
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        button.addActionListener(listener);
        return button;
    }

    protected JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    protected JPanel createFormPanel(Component... components) {
        JPanel formPanel = new JPanel(new GridLayout(0,2,15, 15));
        formPanel.setBackground(SECONDARY_COLOR);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        for (Component component : components) {
            if (component instanceof JLabel) {
                component.setFont(LABEL_FONT);
                component.setForeground(TEXT_COLOR);
            }

            if (component instanceof JTextField) {
                JTextField textField = (JTextField) component;
                textField.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(200, 200, 200), 1),
                        new EmptyBorder(8, 10, 8, 10)
                ));
            }

            formPanel.add(component);
        }
        return formPanel;
    }

    protected JPanel createHeaderPanel(Runnable handleLogout) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logoutButton.setOpaque(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setToolTipText(null); // disable tooltip
        logoutButton.addActionListener(e -> handleLogout.run());

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }


    protected JLabel createLabel(String text, int fontSize, int style) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", style, fontSize));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    protected JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    protected JPanel createTablePanel(DefaultTableModel model, JTable table) {
        table.setModel(model);
        table.setFont(TABLE_FONT);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setAutoCreateRowSorter(true);
        JTableHeader header = table.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(TEXT_COLOR);
        header.setBorder(new EmptyBorder(5, 5, 5, 5));
        header.setForeground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        JPanel panel = new JPanel(new BorderLayout());
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PRIMARY_COLOR, 1, true), // Rounded line border
                new EmptyBorder(10, 10, 10, 10)         // Internal padding
        ));

        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    protected JTabbedPane createTabbedPane(
            Runnable handleAddCustomer,
            Runnable handleEditCustomer,
            Runnable handleRemoveCustomer,
            Runnable handleAddAccounts,
            Runnable handleAccountOperations,
            Runnable handleRemoveAccount,
            DefaultTableModel customerTableModel,
            JTable customerTable,
            DefaultTableModel accountTableModel,
            JTable accountTable
    ) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(SECONDARY_COLOR);
        tabbedPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.addTab("Customers", createCustomerPanel(
            handleAddCustomer, customerTableModel, customerTable, handleEditCustomer, handleRemoveCustomer
        ));
        tabbedPane.addTab("Accounts", createAccountsPanel(
            accountTableModel, accountTable, handleAddAccounts, handleAccountOperations, handleRemoveAccount
        ));
        return tabbedPane;
    }

    protected JPanel createCustomerPanel(Runnable handleAddCustomer, DefaultTableModel model, JTable table, Runnable handleEditCustomer, Runnable handleRemoveCustomer) {  // Fixed parameter order
        JPanel customerPanel = new JPanel(new BorderLayout(0, 15));
        customerPanel.setBackground(SECONDARY_COLOR);
        customerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        customerPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel tablePanel = createTablePanel(model, table);
        customerPanel.add(tablePanel, BorderLayout.CENTER);
        String[] labels = {"Add Customer", "Edit Customer", "Remove Customer"};
        Runnable[] actions = {handleAddCustomer, handleEditCustomer, handleRemoveCustomer}; // Fixed order to match labels
        JPanel buttonPanel = createButtonPanel(labels, actions);
        customerPanel.add(buttonPanel, BorderLayout.SOUTH);
        return customerPanel;
    }

    protected JPanel createAccountsPanel(DefaultTableModel accountTableModel, JTable accountTable, 
        Runnable handleAddAccount, Runnable handleAccountOperations, Runnable handleRemoveAccount) {
    JPanel accountPanel = new JPanel(new BorderLayout(0, 15));
    accountPanel.setBackground(SECONDARY_COLOR);
    accountPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
    accountTable.setModel(accountTableModel);
    accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    // Create a table panel that will contain the account table
    JPanel tablePanel = createTablePanel(accountTableModel, accountTable);
    
    // Add a name to the panel for easier identification
    tablePanel.setName("accountTablePanel");
    
    // Add the table panel to the account panel
    accountPanel.add(tablePanel, BorderLayout.CENTER);
    
    String[] labels = {"Add Account", "Account Operations", "Remove Account"};
    Runnable[] actions = {handleAddAccount, handleAccountOperations, handleRemoveAccount};
    JPanel buttonPanel = createButtonPanel(labels, actions);
    accountPanel.add(buttonPanel, BorderLayout.SOUTH);
    return accountPanel;
}

    protected void showMessage(String message, String title, int messageType) {
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("OptionPane.background", SECONDARY_COLOR);
        UIManager.put("Panel.background", SECONDARY_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    protected void showErrorMessage(String message) {
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("OptionPane.background", SECONDARY_COLOR);
        UIManager.put("Panel.background", SECONDARY_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        showMessage(message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void showInfoMessage(String message) {
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("OptionPane.background", SECONDARY_COLOR);
        UIManager.put("Panel.background", SECONDARY_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void showWarningMessage(String message) {
        showMessage(message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    protected String showInputDialog(String message) {
        UIManager.put("OptionPane.background", SECONDARY_COLOR);
        UIManager.put("Panel.background", SECONDARY_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        return JOptionPane.showInputDialog(this, message);
    }
    protected int showConfirmDialog(String message) {
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("OptionPane.background", SECONDARY_COLOR);
        UIManager.put("Panel.background", SECONDARY_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        return JOptionPane.showConfirmDialog(this, message, "Confirm", JOptionPane.YES_NO_OPTION);
    }

    protected <T> JComboBox<T> createDropdown(List<T> items) {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(LABEL_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT_COLOR);

        for (T item : items) {
            comboBox.addItem(item);
        }

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Customer) {
                    setText(((Customer) value).getName());
                } else if (value instanceof Account) {
                    setText(((Account) value).getName());
                } else if (value != null) {
                    setText(value.toString());
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

        return comboBox;
    }
}