package com.example.hardexpro.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hardexpro.R;
import com.example.hardexpro.databinding.FragmentDashboardBinding;
import com.example.hardexpro.ui.base.MainViewModel;
import com.example.hardexpro.utils.Constants;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private MainViewModel viewModel;
    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        setupRecyclerView();
        observeMetrics();
        setupQuickActions();
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter();
        binding.rvRecentTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRecentTransactions.setAdapter(adapter);

        adapter.setOnLongClickListener(transaction -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete invoice #" + transaction.getInvoiceNumber() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        viewModel.deleteTransaction(transaction);
                        android.widget.Toast
                                .makeText(requireContext(), "Transaction deleted", android.widget.Toast.LENGTH_SHORT)
                                .show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void observeMetrics() {
        // Today's Sales
        viewModel.getTotalSalesByDate(Constants.getCurrentDate()).observe(getViewLifecycleOwner(), sales -> {
            binding.tvTodaySales.setText(String.format("₹ %.2f", sales != null ? sales : 0.0));
        });

        // Low Stock Count
        viewModel.getLowStockItems().observe(getViewLifecycleOwner(), items -> {
            binding.tvLowStockCount.setText(String.format("%d Items", items != null ? items.size() : 0));
        });

        // Recent Transactions
        viewModel.getRecentTransactions().observe(getViewLifecycleOwner(), transactions -> {
            adapter.setTransactions(transactions);
        });
    }

    private void setupQuickActions() {
        binding.btnQuickBill.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_billing);
        });

        binding.btnQuickAdd.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_inventory);
        });

        binding.btnLogout.setOnClickListener(v -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout? Local data will be cleared for security.")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        viewModel.logout();
                        startActivity(new android.content.Intent(requireActivity(),
                                com.example.hardexpro.ui.auth.LoginActivity.class));
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
