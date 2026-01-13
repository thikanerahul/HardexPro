package com.example.hardexpro.ui.inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hardexpro.data.model.Item;
import com.example.hardexpro.databinding.ItemInventoryBinding;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {
    private List<Item> items = new ArrayList<>();
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public InventoryAdapter() {
    }

    public InventoryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Item item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setList(List<Item> items) {
        setItems(items);
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
        holder.binding.tvStock.setText(String.format("%.0f %s", item.getStockQuantity(), item.getUnit()));

        if (item.getStockQuantity() <= item.getLowStockThreshold()) {
            holder.binding.tvLowStock.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvLowStock.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(item);
                return true;
            }
            return false;
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
