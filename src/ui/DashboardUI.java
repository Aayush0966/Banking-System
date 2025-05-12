package ui;

import interfaces.IAuthService;
import services.TransactionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class DashboardUI extends BaseFrame {
    private IAuthService authService;
    private TransactionService transactionService;


    public DashboardUI(IAuthService authService) {
        super("Admin Dashboard");
        this.authService = authService;
    }

    @Override
    protected void setupUI() {
        setSize(800, 600);
        transactionService = new TransactionService();
        JPanel headerPanel = createHeaderPanel("Admin Dashboard", this::handleLogout );
        JTabbedPane tabbedPanel = createTabbedPane(
                this::handleAddCustomer,
                this::handleEditCustomer,
                this::handleRemoveCustomer,
                this::handleAddAccount,
                this::handleEditAccount
        );
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    protected void handleAddCustomer() {

    }

    protected void handleEditCustomer() {

    }

    protected void handleRemoveCustomer() {

    }

    protected void handleAddAccount() {

    }

    protected void handleEditAccount() {

    }

    void handleLogout() {
        authService.logout();
        dispose();
    }
}
