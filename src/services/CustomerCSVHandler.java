package services;

import exceptions.FileReadException;
import exceptions.InvalidDataException;
import interfaces.IFileHandler;
import model.Customer;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerCSVHandler implements IFileHandler<Customer> {
    private final String fileName;
    public CustomerCSVHandler(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void saveData(List<Customer> customers) throws FileReadException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Customer customer : customers) {
                writer.write(String.join(",", customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone()));
                writer.newLine();
            }
        } catch (Exception e) {
            throw  new FileReadException("Error while saving data " + e.getMessage());
        }
    }

    @Override
    public List<Customer> loadData() throws FileReadException, InvalidDataException {
        List<Customer> customers = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return customers;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 4) continue;

                Customer customer = new Customer(data[1], data[2], data[3]);
                customer.setId(data[0]);
                customers.add(customer);
            }
        } catch (Exception e) {
            throw new FileReadException("Error while loading data " + e.getMessage());
        }
        return customers;
    }
}
