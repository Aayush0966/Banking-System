package services;

import interfaces.IAuthService;;

public class AuthService implements IAuthService {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private boolean isAuthenticated = false;


    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public boolean login (String username, String password) {
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            isAuthenticated = true;
            return true;
        }
        return false;

    }

    @Override
    public void logout() {
        isAuthenticated = false;
    }

}
