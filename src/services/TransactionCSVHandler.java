package services;

import exceptions.FileReadException;
import exceptions.InvalidDataException;
import interfaces.IFileHandler;
import model.Transaction;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionCSVHandler implements IFileHandler<Transaction> {
    private final String fileName;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
    public TransactionCSVHandler(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void saveData(List<Transaction> data) throws FileReadException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Transaction transaction : data) {
                writer.write(String.join(",",
                        transaction.getId(),
                        transaction.getName(),
                        transaction.getType(),
                        String.valueOf(transaction.getAmount()),
                        transaction.getSendingAccountId() == null ? "" : transaction.getSendingAccountId(),
                        transaction.getReceivingAccountId() == null ? "": transaction.getReceivingAccountId(),
                        dateFormat.format(transaction.getTimeStamp())));
                writer.newLine();
            }
        } catch (Exception e) {
            throw new FileReadException("Error while saving data" + e.getMessage());
        }
    }

    @Override
    public List<Transaction> loadData() throws FileReadException, InvalidDataException {
        List<Transaction> data = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return data;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 7) continue;

                String id = parts[0];
                String name = parts[1];
                String type = parts[2];
                double amount = Double.parseDouble(parts[3]);
                String sendingAccountId = parts[4].isEmpty() ? "" : parts[4];
                String receivingAccountId = parts[5].isEmpty() ? "" : parts[5];
                Date timeStamp  = new Date();
                try {
                     timeStamp = dateFormat.parse(parts[6]);
                } catch (ParseException e) {
                    System.err.println("Error while parsing date: " + e.getMessage());
                }

                Transaction transaction = new Transaction(type, amount, sendingAccountId, receivingAccountId, timeStamp);
                transaction.setId(id);
                transaction.setName(name);
                data.add(transaction);
            }
        } catch (Exception e) {
            throw new FileReadException("Error while loading data" + e.getMessage());
        } catch (Throwable e) {
            throw new InvalidDataException("Error while loading data" + e.getMessage());
        }
        return data;
    }
}
