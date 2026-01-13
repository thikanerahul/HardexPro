package com.example.hardexpro.ui.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hardexpro.databinding.FragmentInventoryBinding;
import com.example.hardexpro.ui.base.MainViewModel;

public class InventoryFragment extends Fragment {
    private FragmentInventoryBinding binding;
    private MainViewModel viewModel;
    private InventoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        setupRecyclerView();
        observeData();
        setupSearch();

        binding.fabAddItem.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), AddItemActivity.class));
        });

        adapter.setOnItemLongClickListener(item -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete " + item.getName() + "? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        viewModel.deleteItem(item);
                        android.widget.Toast
                                .makeText(requireContext(), "Item deleted", android.widget.Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setupRecyclerView() {
        adapter = new InventoryAdapter();
        binding.rvInventory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvInventory.setAdapter(adapter);
    }

    private void observeData() {
        viewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
        });
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.searchItems(s.toString()).observe(getViewLifecycleOwner(), items -> {
                    adapter.setItems(items);
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
