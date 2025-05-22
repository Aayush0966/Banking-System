package ui;

import interfaces.IAuthService;
import services.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginUI extends BaseFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private IAuthService authService;

    public LoginUI() {
        super("Banking System");
        this.authService = new AuthService();
        init();
    }

    @Override
    protected void setupUI() {
        mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(SECONDARY_COLOR);
        mainPanel.setBorder(new EmptyBorder(50, 70, 50, 70));

        mainPanel.add(createLogoPanel(), BorderLayout.NORTH);
        mainPanel.add(createLoginCard(), BorderLayout.CENTER);
        mainPanel.add(createFooterLabel(), BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(SECONDARY_COLOR);
        logoPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        JLabel bankLogo = new JLabel("MODERN BANKING", JLabel.CENTER);
        bankLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        bankLogo.setForeground(PRIMARY_COLOR);
        JLabel tagline = new JLabel("Secure • Fast • Reliable", JLabel.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        tagline.setForeground(new Color(100, 100, 100));
        logoPanel.add(bankLogo, BorderLayout.CENTER);
        logoPanel.add(tagline, BorderLayout.SOUTH);
        return logoPanel;
    }

    private JPanel createLoginCard() {
        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(30, 30, 30, 30)
        ));
        JLabel title = new JLabel("Login to Your Account", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        JPanel fields = createFormFields();
        JPanel buttons = createButtonSection();
        card.add(title, BorderLayout.NORTH);
        card.add(fields, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createFormFields() {
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 1, 0, 15));
        fieldsPanel.setBackground(Color.WHITE);
        JPanel usernamePanel = new JPanel(new BorderLayout(0, 5));
        usernamePanel.setBackground(Color.WHITE);
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(LABEL_FONT);
        usernameLabel.setForeground(TEXT_COLOR);
        usernameField = createTextField();
        usernamePanel.add(usernameLabel, BorderLayout.NORTH);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        JPanel passwordPanel = new JPanel(new BorderLayout(0, 5));
        passwordPanel.setBackground(Color.WHITE);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(LABEL_FONT);
        passwordLabel.setForeground(TEXT_COLOR);
        passwordField = createPasswordField();
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        fieldsPanel.add(usernamePanel);
        fieldsPanel.add(passwordPanel);
        return fieldsPanel;
    }

    private JPanel createButtonSection() {
        JPanel buttonSection = new JPanel(new BorderLayout());
        buttonSection.setBackground(Color.WHITE);
        loginButton = createButton("Login", PRIMARY_COLOR, ACCENT_COLOR, e -> handleLogin());
        JPanel loginPanel = new JPanel(new BorderLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        loginPanel.add(loginButton, BorderLayout.CENTER);
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setBackground(Color.WHITE);
        JLabel forgotPassword = getJLabel();
        linkPanel.add(forgotPassword);
        buttonSection.add(loginPanel, BorderLayout.CENTER);
        buttonSection.add(linkPanel, BorderLayout.SOUTH);
        return buttonSection;
    }

    private JLabel getJLabel() {
        JLabel forgotPassword = new JLabel("Forgot Password?");
        forgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgotPassword.setForeground(ACCENT_COLOR);
        forgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPassword.setText("<html><u>Forgot Password?</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPassword.setText("Forgot Password?");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showInfoMessage("Password reset functionality will be available soon.");
            }
        });
        return forgotPassword;
    }

    private JLabel createFooterLabel() {
        JLabel footer = new JLabel("© 2025 Modern Banking System. All rights reserved.", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setForeground(new Color(140, 140, 140));
        footer.setBorder(new EmptyBorder(20, 0, 0, 0));
        return footer;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Username and password are required");
            return;
        }

        showInfoMessage("Login Successful");
        new DashboardUI();
        dispose();
    }
}
