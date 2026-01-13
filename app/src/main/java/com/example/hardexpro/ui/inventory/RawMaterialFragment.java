package com.example.hardexpro.ui.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hardexpro.data.model.Item;
import com.example.hardexpro.databinding.FragmentRawMaterialBinding;
import com.example.hardexpro.ui.base.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class RawMaterialFragment extends Fragment {
    private FragmentRawMaterialBinding binding;
    private MainViewModel viewModel;
    private InventoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentRawMaterialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        adapter = new InventoryAdapter(item -> {
            // Edit Raw Material logic here
        });

        binding.rvRawMaterials.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRawMaterials.setAdapter(adapter);

        viewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            List<Item> rawMaterials = new ArrayList<>();
            for (Item item : items) {
                if (item.isRawMaterial()) {
                    rawMaterials.add(item);
                }
            }
            adapter.setList(rawMaterials);
        });

        binding.btnGoToInventory.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view).navigate(com.example.hardexpro.R.id.nav_inventory);
        });

        binding.fabAddRaw.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(requireActivity(), AddItemActivity.class);
            intent.putExtra("isRawMaterial", true);
            startActivity(intent);
        });

        adapter.setOnItemLongClickListener(item -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Material")
                    .setMessage("Are you sure you want to delete " + item.getName() + "? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        viewModel.deleteItem(item);
                        android.widget.Toast
                                .makeText(requireContext(), "Material deleted", android.widget.Toast.LENGTH_SHORT)
                                .show();
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
