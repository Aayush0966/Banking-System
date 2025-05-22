package interfaces;

import exceptions.FileReadException;
import exceptions.InvalidDataException;
import model.Account;
import model.Customer;

import java.util.List;

public interface IFileHandler<T> {
    void saveData(List<T> data) throws FileReadException;
    List<T> loadData() throws FileReadException, InvalidDataException;

}