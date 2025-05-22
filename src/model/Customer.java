package model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends BankEntity {
    private String email;
    private String phone;
    private List<Account> accounts;

    public Customer(String name, String email, String phone) {
        super(name);
        setEmail(email);
        setPhone(phone);
        this.accounts = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        
        String digitsOnly = phone.replaceAll("[^0-9]", "");
        
        if (digitsOnly.length() != 10) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits");
        }
        
        this.phone = phone;
    }

   public int getAccountsCount() {
        return accounts.size();
    }
    
    public boolean hasActiveAccounts() {
    return !accounts.isEmpty();
}
    

    public List<Account> getAccounts() {
        return accounts;
    }
    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", accounts=" + accounts.size() +
                '}';
    }


}