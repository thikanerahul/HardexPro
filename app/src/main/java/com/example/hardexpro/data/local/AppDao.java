package com.example.hardexpro.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hardexpro.data.model.Customer;
import com.example.hardexpro.data.model.Item;
import com.example.hardexpro.data.model.Supplier;
import com.example.hardexpro.data.model.Transaction;

import java.util.List;

@Dao
public interface AppDao {

    // Item Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertItem(Item item);

    @Update
    void updateItem(Item item);

    @Delete
    void deleteItem(Item item);

    @Query("SELECT * FROM items ORDER BY name ASC")
    LiveData<List<Item>> getAllItems();

    @Query("SELECT * FROM items WHERE category = :category ORDER BY name ASC")
    LiveData<List<Item>> getItemsByCategory(String category);

    @Query("SELECT * FROM items WHERE stockQuantity <= lowStockThreshold")
    LiveData<List<Item>> getLowStockItems();

    @Query("SELECT * FROM items WHERE id = :id")
    Item getItemById(long id);

    @Query("SELECT * FROM items WHERE barcode = :barcode")
    Item getItemByBarcode(String barcode);

    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%'")
    LiveData<List<Item>> searchItems(String query);

    // Customer Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCustomer(Customer customer);

    @Update
    void updateCustomer(Customer customer);

    @Delete
    void deleteCustomer(Customer customer);

    @Query("SELECT * FROM customers ORDER BY name ASC")
    LiveData<List<Customer>> getAllCustomers();

    @Query("SELECT * FROM customers WHERE outstandingBalance > 0 ORDER BY outstandingBalance DESC")
    LiveData<List<Customer>> getCreditorCustomers();

    @Query("SELECT * FROM customers WHERE id = :id")
    Customer getCustomerById(long id);

    // Supplier Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSupplier(Supplier supplier);

    @Update
    void updateSupplier(Supplier supplier);

    @Delete
    void deleteSupplier(Supplier supplier);

    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    LiveData<List<Supplier>> getAllSuppliers();

    // Transaction Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTransaction(Transaction transaction);

    @Delete
    void deleteTransaction(Transaction transaction);

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    LiveData<List<Transaction>> getTransactionsByCustomer(long customerId);

    @Query("SELECT * FROM transactions WHERE date = :date ORDER BY timestamp DESC")
    LiveData<List<Transaction>> getTransactionsByDate(String date);

    @Query("SELECT SUM(totalAmount) FROM transactions WHERE date = :date")
    LiveData<Double> getTotalSalesByDate(String date);

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 10")
    LiveData<List<Transaction>> getRecentTransactions();
}
