package com.example.hardexpro.ui.customer;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hardexpro.data.model.Customer;
import com.example.hardexpro.databinding.ActivityCustomerDetailsBinding;
import com.example.hardexpro.ui.base.BaseActivity;
import com.example.hardexpro.ui.base.MainViewModel;
import com.example.hardexpro.ui.dashboard.TransactionAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CustomerDetailsActivity extends BaseActivity {
    private ActivityCustomerDetailsBinding binding;
    private MainViewModel viewModel;
    private TransactionAdapter adapter;
    private Customer customer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        customer = (Customer) getIntent().getSerializableExtra("customer");
        if (customer == null) {
            finish();
            return;
        }

        setupToolbar();
        displayCustomerInfo();
        setupRecyclerView();
        observeHistory();

        binding.btnCollectPayment.setOnClickListener(v -> showRepaymentDialog());
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void displayCustomerInfo() {
        binding.tvCustomerName.setText(customer.getName());
        binding.tvMobile.setText(customer.getMobile());
        binding.tvAddress.setText(customer.getAddress());
        updateBalanceDisplay();
    }

    private void updateBalanceDisplay() {
        binding.tvOutstanding.setText(String.format("₹ %.2f", customer.getOutstandingBalance()));
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHistory.setAdapter(adapter);

        adapter.setOnLongClickListener(transaction -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete invoice #" + transaction.getInvoiceNumber() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        viewModel.deleteTransaction(transaction);
                        Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void observeHistory() {
        viewModel.getTransactionsByCustomer(customer.getId()).observe(this, transactions -> {
            adapter.setTransactions(transactions);
        });
    }

    private void showRepaymentDialog() {
        EditText etAmount = new EditText(this);
        etAmount.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etAmount.setHint("Enter amount collected");

        new MaterialAlertDialogBuilder(this)
                .setTitle("Collect Payment")
                .setMessage("Current Dues: ₹ " + String.format("%.2f", customer.getOutstandingBalance()))
                .setView(etAmount)
                .setPositiveButton("Collect", (dialog, which) -> {
                    String amountStr = etAmount.getText().toString();
                    if (!amountStr.isEmpty()) {
                        double amount = Double.parseDouble(amountStr);
                        double newBalance = customer.getOutstandingBalance() - amount;
                        customer.setOutstandingBalance(newBalance);
                        viewModel.updateCustomer(customer);
                        updateBalanceDisplay();
                        Toast.makeText(this, "Payment Recorded Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }
}
