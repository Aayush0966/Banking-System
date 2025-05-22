package model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends BankEntity {
    private String email;
    private String phone;
    private List<Account> accounts;

    public Customer(String name, String email, String phone) {
        super(name);
        this.email = email;
        this.phone = phone;
        this.accounts = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
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