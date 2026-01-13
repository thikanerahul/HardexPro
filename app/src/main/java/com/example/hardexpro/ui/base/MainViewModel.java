package com.example.hardexpro.ui.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.hardexpro.data.model.Customer;
import com.example.hardexpro.data.model.Item;
import com.example.hardexpro.data.model.Supplier;
import com.example.hardexpro.data.model.Transaction;
import com.example.hardexpro.data.repository.MainRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private MainRepository repository;
    private LiveData<List<Item>> allItems;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new MainRepository(application);
        allItems = repository.getAllItems();
    }

    // Item Operations
    public void insertItem(Item item) {
        repository.insertItem(item);
    }

    public void updateItem(Item item) {
        repository.updateItem(item);
    }

    public void deleteItem(Item item) {
        repository.deleteItem(item);
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public LiveData<List<Item>> searchItems(String query) {
        return repository.searchItems(query);
    }

    public LiveData<List<Item>> getLowStockItems() {
        return repository.getLowStockItems();
    }

    // Transaction Operations
    public void insertTransaction(Transaction transaction) {
        repository.insertTransaction(transaction);
    }

    public void deleteTransaction(Transaction transaction) {
        repository.deleteTransaction(transaction);
    }

    public LiveData<Double> getTotalSalesByDate(String date) {
        return repository.getTotalSalesByDate(date);
    }

    public LiveData<List<Transaction>> getRecentTransactions() {
        return repository.getRecentTransactions();
    }

    public LiveData<List<Transaction>> getTransactionsByCustomer(long customerId) {
        return repository.getTransactionsByCustomer(customerId);
    }

    // Customer Operations
    public void insertCustomer(Customer customer) {
        repository.insertCustomer(customer);
    }

    public void updateCustomer(Customer customer) {
        repository.updateCustomer(customer);
    }

    public void deleteCustomer(Customer customer) {
        repository.deleteCustomer(customer);
    }

    public LiveData<List<Customer>> getAllCustomers() {
        return repository.getAllCustomers();
    }

    // Supplier Operations
    public void insertSupplier(Supplier supplier) {
        repository.insertSupplier(supplier);
    }

    public void updateSupplier(Supplier supplier) {
        repository.updateSupplier(supplier);
    }

    public void deleteSupplier(Supplier supplier) {
        repository.deleteSupplier(supplier);
    }

    public LiveData<List<Supplier>> getAllSuppliers() {
        return repository.getAllSuppliers();
    }

    public void logout() {
        repository.logout();
    }
}
