package ui;

import interfaces.IAuthService;
import services.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginUI extends BaseFrame{
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private IAuthService authService;

    public LoginUI() {
        super("Banking System - Login");
        System.out.println("LoginUI initialized");
        this.authService = new AuthService();
        init();
    }

    @Override
    protected void setupUI() {
        mainPanel=new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20,30,20,30));         usernameField = new JTextField(15);
         passwordField = new JPasswordField(15);
        mainPanel.add(createFormPanel(
                new JLabel("Username: "), usernameField,
                new JLabel("Password: "), passwordField
        ));
        loginButton = new JButton("Login");
        mainPanel.add(loginButton);
        loginButton.addActionListener(e -> handleLogin());
        setContentPane(mainPanel);
    }


    private void handleLogin() {
        System.out.println("LoginUI handleLogin");
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

//        if (username.isEmpty() || password.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Username or Password is empty", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }

//        if (authService.login(username, password)) {
            JOptionPane.showMessageDialog(this, "Login Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
            new DashboardUI(authService);
            dispose();

//        }
//        else {
//            JOptionPane.showMessageDialog(this, "Login Failed", "Error", JOptionPane.ERROR_MESSAGE);
//            passwordField.setText("");
//        }
    }
}