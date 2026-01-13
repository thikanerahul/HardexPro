package com.example.hardexpro.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.hardexpro.data.local.AppDao;
import com.example.hardexpro.data.local.AppDatabase;
import com.example.hardexpro.data.model.Customer;
import com.example.hardexpro.data.model.Item;
import com.example.hardexpro.data.model.Supplier;
import com.example.hardexpro.data.model.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainRepository {
    private AppDao appDao;
    private ExecutorService executorService;
    private DatabaseReference firebaseRef;
    private String userId;
    private android.app.Application application;

    public MainRepository(android.app.Application application) {
        this.application = application;
        AppDatabase db = AppDatabase.getInstance(application);
        appDao = db.appDao();
        executorService = Executors.newFixedThreadPool(4);

        firebaseRef = FirebaseDatabase.getInstance().getReference();

        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance()
                .getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                userId = email.replace(".", "_");
            } else {
                userId = user.getUid();
            }
        } else {
            userId = "admin_user"; // Fallback for safety, though login should be enforced
        }
    }

    // Generic Firebase Sync Helper
    private void syncToFirebase(String node, String id, Object data) {
        if (userId != null && !userId.equals("admin_user")) {
            firebaseRef.child("users").child(userId).child(node).child(id).setValue(data);
        }
    }

    public void logout() {
        if (userId != null && !userId.equals("admin_user")) {
            firebaseRef.child("users").child(userId).onDisconnect();
        }
        executorService.execute(() -> {
            AppDatabase.getInstance(application).clearAllTables();
        });
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
    }

    // Item Operations
    public void insertItem(Item item) {
        executorService.execute(() -> {
            long id = appDao.insertItem(item);
            item.setId(id);
            syncToFirebase("items", String.valueOf(id), item);
        });
    }

    public void updateItem(Item item) {
        executorService.execute(() -> {
            appDao.updateItem(item);
            syncToFirebase("items", String.valueOf(item.getId()), item);
        });
    }

    public void deleteItem(Item item) {
        executorService.execute(() -> {
            appDao.deleteItem(item);
            if (userId != null) {
                firebaseRef.child("users").child(userId).child("items").child(String.valueOf(item.getId()))
                        .removeValue();
            }
        });
    }

    public LiveData<List<Item>> getAllItems() {
        return appDao.getAllItems();
    }

    public LiveData<List<Item>> searchItems(String query) {
        return appDao.searchItems("%" + query + "%");
    }

    public LiveData<List<Item>> getLowStockItems() {
        return appDao.getLowStockItems();
    }

    // Transaction Operations
    public void insertTransaction(Transaction transaction) {
        executorService.execute(() -> {
            long id = appDao.insertTransaction(transaction);
            transaction.setId(id);
            syncToFirebase("transactions", transaction.getInvoiceNumber(), transaction);
        });
    }

    public void deleteTransaction(Transaction transaction) {
        executorService.execute(() -> {
            appDao.deleteTransaction(transaction);
            if (userId != null) {
                firebaseRef.child("users").child(userId).child("transactions").child(transaction.getInvoiceNumber())
                        .removeValue();
            }
        });
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return appDao.getAllTransactions();
    }

    public LiveData<Double> getTotalSalesByDate(String date) {
        return appDao.getTotalSalesByDate(date);
    }

    public LiveData<List<Transaction>> getRecentTransactions() {
        return appDao.getRecentTransactions();
    }

    public LiveData<List<Transaction>> getTransactionsByCustomer(long customerId) {
        return appDao.getTransactionsByCustomer(customerId);
    }

    // Customer Operations
    public void insertCustomer(Customer customer) {
        executorService.execute(() -> {
            long id = appDao.insertCustomer(customer);
            customer.setId(id);
            syncToFirebase("customers", String.valueOf(id), customer);
        });
    }

    public LiveData<List<Customer>> getAllCustomers() {
        return appDao.getAllCustomers();
    }

    public void updateCustomer(Customer customer) {
        executorService.execute(() -> {
            appDao.updateCustomer(customer);
            syncToFirebase("customers", String.valueOf(customer.getId()), customer);
        });
    }

    public void deleteCustomer(Customer customer) {
        executorService.execute(() -> {
            appDao.deleteCustomer(customer);
            if (userId != null) {
                firebaseRef.child("users").child(userId).child("customers").child(String.valueOf(customer.getId()))
                        .removeValue();
            }
        });
    }

    // Supplier Operations
    public void insertSupplier(Supplier supplier) {
        executorService.execute(() -> {
            long id = appDao.insertSupplier(supplier);
            supplier.setId(id);
            syncToFirebase("suppliers", String.valueOf(id), supplier);
        });
    }

    public void updateSupplier(Supplier supplier) {
        executorService.execute(() -> {
            appDao.updateSupplier(supplier);
            syncToFirebase("suppliers", String.valueOf(supplier.getId()), supplier);
        });
    }

    public void deleteSupplier(Supplier supplier) {
        executorService.execute(() -> {
            appDao.deleteSupplier(supplier);
            if (userId != null) {
                firebaseRef.child("users").child(userId).child("suppliers").child(String.valueOf(supplier.getId()))
                        .removeValue();
            }
        });
    }

    public LiveData<List<Supplier>> getAllSuppliers() {
        return appDao.getAllSuppliers();
    }
}
