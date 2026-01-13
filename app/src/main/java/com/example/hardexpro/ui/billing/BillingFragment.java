package com.example.hardexpro.ui.billing;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hardexpro.data.model.CartItem;
import com.example.hardexpro.data.model.Customer;
import com.example.hardexpro.data.model.Item;
import com.example.hardexpro.data.model.Transaction;
import com.example.hardexpro.databinding.FragmentBillingBinding;
import com.example.hardexpro.databinding.LayoutItemSearchBinding;
import com.example.hardexpro.ui.base.MainViewModel;
import com.example.hardexpro.ui.customer.CustomerAdapter;
import com.example.hardexpro.utils.Constants;
import com.example.hardexpro.utils.PdfGenerator;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BillingFragment extends Fragment {
    private FragmentBillingBinding binding;
    private MainViewModel viewModel;
    private CartAdapter cartAdapter;
    private BottomSheetDialog searchDialog;
    private ItemSearchAdapter searchAdapter;
    private Customer selectedCustomer = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentBillingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        setupRecyclerView();
        setupSearchDialog();
        calculateBill();

        binding.fabAddItem.setOnClickListener(v -> searchDialog.show());
        binding.btnCheckout.setOnClickListener(v -> {
            if (cartAdapter.getCartItems().isEmpty()) {
                Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            showPaymentDialog();
        });

        binding.btnSelectCustomer.setOnClickListener(v -> showCustomerSelectionDialog());
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter();
        binding.rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCart.setAdapter(cartAdapter);

        cartAdapter.setOnCartChangeListener(new CartAdapter.OnCartChangeListener() {
            @Override
            public void onQuantityChanged() {
                calculateBill();
            }

            @Override
            public void onItemDeleted(int position) {
                calculateBill();
            }
        });
    }

    private void setupSearchDialog() {
        searchDialog = new BottomSheetDialog(requireContext());
        LayoutItemSearchBinding searchBinding = LayoutItemSearchBinding.inflate(getLayoutInflater());
        searchDialog.setContentView(searchBinding.getRoot());

        searchAdapter = new ItemSearchAdapter();
        searchBinding.rvSearchItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        searchBinding.rvSearchItems.setAdapter(searchAdapter);

        viewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            searchAdapter.setItems(items);
        });

        searchBinding.tvSearchTitle.setText("Select Item");
        searchBinding.etSearch.setHint("Search items...");

        searchAdapter.setOnItemSelectListener(item -> {
            cartAdapter.addOrUpdateItem(new CartItem(item, 1));
            calculateBill();
            searchDialog.dismiss();
        });

        searchBinding.etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.searchItems(s.toString()).observe(getViewLifecycleOwner(), items -> {
                    searchAdapter.setItems(items);
                });
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
    }

    private void showCustomerSelectionDialog() {
        BottomSheetDialog customerDialog = new BottomSheetDialog(requireContext());
        LayoutItemSearchBinding searchBinding = LayoutItemSearchBinding.inflate(getLayoutInflater());
        customerDialog.setContentView(searchBinding.getRoot());

        searchBinding.tvSearchTitle.setText("Select Customer");
        searchBinding.etSearch.setHint("Search Customer...");
        CustomerAdapter customerAdapter = new CustomerAdapter(customer -> {
            selectedCustomer = customer;
            binding.tvSelectedCustomer.setText(customer.getName());
            customerDialog.dismiss();
        });

        searchBinding.rvSearchItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        searchBinding.rvSearchItems.setAdapter(customerAdapter);

        viewModel.getAllCustomers().observe(getViewLifecycleOwner(), customers -> {
            customerAdapter.setList(customers);
        });

        searchBinding.etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.getAllCustomers().observe(getViewLifecycleOwner(), customers -> {
                    List<Customer> filtered = new ArrayList<>();
                    for (Customer c : customers) {
                        if (c.getName().toLowerCase().contains(s.toString().toLowerCase()) ||
                                c.getMobile().contains(s.toString())) {
                            filtered.add(c);
                        }
                    }
                    customerAdapter.setList(filtered);
                });
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });

        customerDialog.show();
    }

    private double getCurrentTotal() {
        double total = 0;
        for (CartItem cartItem : cartAdapter.getCartItems()) {
            total += cartItem.getTotal() * (1 + cartItem.getItem().getTaxPercentage() / 100);
        }
        return total;
    }

    private void calculateBill() {
        binding.tvTotalAmount.setText(String.format("₹ %.2f", getCurrentTotal()));
    }

    private void showPaymentDialog() {
        double totalAmount = getCurrentTotal();
        android.widget.EditText etPaidAmount = new android.widget.EditText(requireContext());
        etPaidAmount.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etPaidAmount.setHint("Enter amount paid");
        etPaidAmount.setText(String.valueOf(totalAmount));

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirm Payment")
                .setMessage("Total Amount: ₹ " + String.format("%.2f", totalAmount))
                .setView(etPaidAmount)
                .setPositiveButton("Checkout", (dialog, which) -> {
                    String paidStr = etPaidAmount.getText().toString();
                    double paidAmount = paidStr.isEmpty() ? 0 : Double.parseDouble(paidStr);
                    generateBill(paidAmount);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void generateBill(double amountPaid) {
        List<CartItem> cartItems = cartAdapter.getCartItems();
        double totalAmount = getCurrentTotal();
        double subtotal = 0;
        double taxTotal = 0;

        for (CartItem ci : cartItems) {
            subtotal += ci.getTotal();
            taxTotal += (ci.getTotal() * ci.getItem().getTaxPercentage() / 100);

            // Update Stock
            Item item = ci.getItem();
            item.setStockQuantity(item.getStockQuantity() - ci.getQuantity());
            viewModel.updateItem(item);
        }

        Transaction transaction = new Transaction();
        transaction.setInvoiceNumber(Constants.generateInvoiceNumber());
        transaction.setDate(Constants.getCurrentDate());
        transaction.setCustomerId(selectedCustomer != null ? selectedCustomer.getId() : 0);
        transaction.setCustomerName(selectedCustomer != null ? selectedCustomer.getName() : "Walk-in Customer");
        transaction.setSubtotal(subtotal);
        transaction.setTax(taxTotal);
        transaction.setTotalAmount(totalAmount);
        transaction.setAmountPaid(amountPaid);

        if (amountPaid >= totalAmount) {
            transaction.setStatus("Paid");
        } else if (amountPaid > 0) {
            transaction.setStatus("Partial");
        } else {
            transaction.setStatus("Pending");
        }

        transaction.setPaymentMode("Cash");
        transaction.setItemsJson(new Gson().toJson(cartItems));

        viewModel.insertTransaction(transaction);

        // Update Customer Outstanding Balance if it's a known customer
        if (selectedCustomer != null && amountPaid < totalAmount) {
            double remaining = totalAmount - amountPaid;
            selectedCustomer.setOutstandingBalance(selectedCustomer.getOutstandingBalance() + remaining);
            viewModel.updateCustomer(selectedCustomer);
        }

        File pdfFile = PdfGenerator.generateInvoice(requireContext(), transaction, cartItems);
        if (pdfFile != null) {
            PdfGenerator.sharePdf(requireContext(), pdfFile);
        }

        Toast.makeText(requireContext(), "Bill Generated Successfully", Toast.LENGTH_SHORT).show();

        // Reset
        selectedCustomer = null;
        binding.tvSelectedCustomer.setText("Walk-in Customer");
        cartAdapter.setCartItems(new ArrayList<>());
        calculateBill();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
