package com.example.hardexpro.ui.inventory;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;

import androidx.lifecycle.ViewModelProvider;

import com.example.hardexpro.data.model.Item;
import com.example.hardexpro.databinding.ActivityAddItemBinding;
import com.example.hardexpro.ui.base.BaseActivity;
import com.example.hardexpro.ui.base.MainViewModel;

public class AddItemActivity extends BaseActivity {
    private ActivityAddItemBinding binding;
    private MainViewModel viewModel;
    private boolean isRawMaterial = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isRawMaterial = getIntent().getBooleanExtra("isRawMaterial", false);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (isRawMaterial) {
                getSupportActionBar().setTitle("Add Raw Material");
            }
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        setupDropdowns();
        binding.btnSave.setOnClickListener(v -> saveItem());
    }

    private void setupDropdowns() {
        String[] categories;
        if (isRawMaterial) {
            categories = new String[] { "Sand", "Gravel", "Cement", "Clinker", "Additives", "Fuel & Oil" };
        } else {
            categories = new String[] { "Cement", "Steel rods", "Pipes", "Paints", "Electrical items",
                    "Tools & machinery" };
        }
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        binding.actvCategory.setAdapter(catAdapter);

        String[] units = { "kg", "piece", "meter", "bag", "ton" };
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, units);
        binding.actvUnit.setAdapter(unitAdapter);
    }

    private void saveItem() {
        String name = binding.etName.getText().toString().trim();
        String category = binding.actvCategory.getText().toString().trim();
        String stockStr = binding.etStock.getText().toString().trim();
        String unit = binding.actvUnit.getText().toString().trim();
        String purchasePriceStr = binding.etPurchasePrice.getText().toString().trim();
        String sellingPriceStr = binding.etSellingPrice.getText().toString().trim();
        String lowStockStr = binding.etLowStock.getText().toString().trim();
        String barcode = binding.etBarcode.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.etName.setError("Name is required");
            return;
        }

        Item item = new Item();
        item.setName(name);
        item.setCategory(category);
        item.setStockQuantity(TextUtils.isEmpty(stockStr) ? 0 : Double.parseDouble(stockStr));
        item.setUnit(unit);
        item.setPurchasePrice(TextUtils.isEmpty(purchasePriceStr) ? 0 : Double.parseDouble(purchasePriceStr));
        item.setSellingPrice(TextUtils.isEmpty(sellingPriceStr) ? 0 : Double.parseDouble(sellingPriceStr));
        item.setLowStockThreshold(TextUtils.isEmpty(lowStockStr) ? 0 : Double.parseDouble(lowStockStr));
        item.setBarcode(barcode);
        item.setRawMaterial(isRawMaterial);

        viewModel.insertItem(item);
        showToast(isRawMaterial ? "Raw Material saved successfully" : "Item saved successfully");
        finish();
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }
}
