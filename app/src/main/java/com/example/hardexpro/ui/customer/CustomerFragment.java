package com.example.hardexpro.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hardexpro.databinding.FragmentCustomersBinding;
import com.example.hardexpro.ui.base.MainViewModel;

public class CustomerFragment extends Fragment {
    private FragmentCustomersBinding binding;
    private MainViewModel viewModel;
    private CustomerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        adapter = new CustomerAdapter(customer -> {
            android.content.Intent intent = new android.content.Intent(requireActivity(),
                    CustomerDetailsActivity.class);
            intent.putExtra("customer", customer);
            startActivity(intent);
        });

        adapter.setOnLongClickListener(customer -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Customer")
                    .setMessage(
                            "Are you sure you want to delete " + customer.getName() + "? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        viewModel.deleteCustomer(customer);
                        android.widget.Toast
                                .makeText(requireContext(), "Customer deleted", android.widget.Toast.LENGTH_SHORT)
                                .show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        binding.rvCustomers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCustomers.setAdapter(adapter);

        viewModel.getAllCustomers().observe(getViewLifecycleOwner(), customers -> {
            adapter.setList(customers);
        });

        binding.fabAddCustomer.setOnClickListener(v -> {
            startActivity(new android.content.Intent(requireActivity(), AddCustomerActivity.class));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
