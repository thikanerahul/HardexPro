package com.example.hardexpro.ui.billing;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hardexpro.data.model.Item;
import com.example.hardexpro.databinding.ItemInventoryBinding;

import java.util.ArrayList;
import java.util.List;

public class ItemSearchAdapter extends RecyclerView.Adapter<ItemSearchAdapter.ViewHolder> {
    private List<Item> items = new ArrayList<>();
    private OnItemSelectListener listener;

    public interface OnItemSelectListener {
        void onItemSelected(Item item);
    }

    public void setOnItemSelectListener(OnItemSelectListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInventoryBinding binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.binding.tvItemName.setText(item.getName());
        holder.binding.tvCategory.setText(item.getCategory());
        holder.binding.tvPrice.setText(String.format("₹ %.2f", item.getSellingPrice()));
        holder.binding.tvStock.setText(String.format("Stock: %.0f", item.getStockQuantity()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onItemSelected(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemInventoryBinding binding;

        ViewHolder(ItemInventoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
