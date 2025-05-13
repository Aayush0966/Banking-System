package services;

import exceptions.FileReadException;
import exceptions.InvalidDataException;
import interfaces.IFileHandler;
import model.Account;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AccountCSVHandler implements IFileHandler<Account> {
    private final String fileName;

    public AccountCSVHandler(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Account> loadData() throws FileReadException, InvalidDataException {
        List<Account> accounts = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return accounts;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 5) continue;

                Account account = new Account(data[1], data[2], data[3]);
                account.setId(data[0]);
                account.setBalance(Double.parseDouble(data[4]));
                accounts.add(account);
            }
        } catch (Exception e) {
            throw new FileReadException("Error while reading file " + fileName + e.getMessage());

        } catch (Throwable e) {
            throw new InvalidDataException("Error while reading file " + fileName + e.getMessage());
        }
        return accounts;
    }

    @Override
    public void saveData(List<Account> accounts) throws FileReadException {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Account account : accounts) {
            writer.write(String.join(",", account.getId(), account.getName(), account.getAccountNum(), account.getCustomerId(), String.valueOf(account.getBalance())));
            writer.newLine();
            }
        } catch (Exception e) {
            throw new FileReadException("Error while writing file " + fileName + e.getMessage());
        }
    }
}
