package com.example.hardexpro.ui.reports;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hardexpro.data.model.Item;
import com.example.hardexpro.databinding.FragmentReportsBinding;
import com.example.hardexpro.ui.base.MainViewModel;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsFragment extends Fragment {
    private FragmentReportsBinding binding;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        setupBarChart();
        setupPieChart();
    }

    private void setupBarChart() {
        // Mock data for sales trend (Ideally this comes from DB)
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 1500));
        entries.add(new BarEntry(1, 2800));
        entries.add(new BarEntry(2, 2200));
        entries.add(new BarEntry(3, 3500));
        entries.add(new BarEntry(4, 3000));
        entries.add(new BarEntry(5, 4200));
        entries.add(new BarEntry(6, 3800));

        BarDataSet dataSet = new BarDataSet(entries, "Daily Sales");
        dataSet.setColor(Color.parseColor("#43A047")); // Primary Green
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        binding.barChart.setData(barData);
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.animateY(1000);
        binding.barChart.invalidate();
    }

    private void setupPieChart() {
        viewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            if (items == null || items.isEmpty())
                return;

            Map<String, Integer> categoryMap = new HashMap<>();
            for (Item item : items) {
                String cat = item.getCategory();
                categoryMap.put(cat, categoryMap.getOrDefault(cat, 0) + 1);
            }

            List<PieEntry> entries = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }

            PieDataSet dataSet = new PieDataSet(entries, "Categories");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setValueTextSize(12f);

            PieData pieData = new PieData(dataSet);
            binding.pieChart.setData(pieData);
            binding.pieChart.getDescription().setEnabled(false);
            binding.pieChart.setCenterText("Inventory Mix");
            binding.pieChart.animateXY(1000, 1000);
            binding.pieChart.invalidate();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
