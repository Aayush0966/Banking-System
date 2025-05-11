package services;

import exceptions.FileReadException;
import exceptions.InvalidDataException;
import interfaces.IFileHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler<T> implements IFileHandler<T> {
    private final String fileName;

    public FileHandler(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void saveData(List<T> data) throws FileReadException {
        try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(fileName))) {
            writer.writeObject(new ArrayList<>(data));
        } catch (FileNotFoundException e) {
            throw new FileReadException("Error saving data to file: " + fileName, e);
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong: "  + e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> loadData() throws FileReadException, InvalidDataException {
       File file = new File(fileName);
       if (!file.exists()) {
           return new ArrayList<>();
       }
       try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(file))) {
           Object obj = reader.readObject();
           if (!(obj instanceof ArrayList)) {
               throw new InvalidDataException("Data format in file is invalid: " + fileName);
           }
           return (List<T>) obj;
       } catch (IOException | ClassNotFoundException e) {
           throw new FileReadException("Error reading data from file: "+ fileName, e);
       }
    }
}
