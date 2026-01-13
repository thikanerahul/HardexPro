package com.example.hardexpro.ui.customer;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.lifecycle.ViewModelProvider;

import com.example.hardexpro.data.model.Customer;
import com.example.hardexpro.databinding.ActivityAddCustomerBinding;
import com.example.hardexpro.ui.base.BaseActivity;
import com.example.hardexpro.ui.base.MainViewModel;

public class AddCustomerActivity extends BaseActivity {
    private ActivityAddCustomerBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        binding.btnSave.setOnClickListener(v -> saveCustomer());
    }

    private void saveCustomer() {
        String name = binding.etName.getText().toString().trim();
        String mobile = binding.etMobile.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String balanceStr = binding.etBalance.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.etName.setError("Name is required");
            return;
        }

        Customer customer = new Customer();
        customer.setName(name);
        customer.setMobile(mobile);
        customer.setAddress(address);
        customer.setOutstandingBalance(TextUtils.isEmpty(balanceStr) ? 0 : Double.parseDouble(balanceStr));

        viewModel.insertCustomer(customer);
        showToast("Customer saved successfully");
        finish();
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }
}
