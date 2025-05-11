package interfaces;

public interface IAuthService {
    boolean login(String username, String password);
    boolean isAuthenticated();
    void logout();
}