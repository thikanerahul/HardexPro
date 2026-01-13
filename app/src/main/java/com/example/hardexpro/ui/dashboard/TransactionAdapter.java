package com.example.hardexpro.ui.dashboard;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hardexpro.data.model.Transaction;
import com.example.hardexpro.databinding.ItemTransactionBinding;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactions = new ArrayList<>();
    private OnTransactionLongClickListener longClickListener;

    public interface OnTransactionLongClickListener {
        void onTransactionLongClick(Transaction transaction);
    }

    public void setOnLongClickListener(OnTransactionLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.binding.tvInvoiceNo.setText(transaction.getInvoiceNumber());
        holder.binding.tvCustomerName.setText(transaction.getCustomerName());
        holder.binding.tvAmount.setText(String.format("₹ %.2f", transaction.getTotalAmount()));
        holder.binding.tvDate.setText(transaction.getDate());

        // Status Chip
        String status = transaction.getStatus();
        holder.binding.chipStatus.setText(status);
        if ("Paid".equalsIgnoreCase(status)) {
            holder.binding.chipStatus.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(
                    holder.itemView.getContext().getResources().getColor(com.example.hardexpro.R.color.success)));
        } else if ("Partial".equalsIgnoreCase(status)) {
            holder.binding.chipStatus.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(
                    holder.itemView.getContext().getResources().getColor(com.example.hardexpro.R.color.primary)));
        } else {
            holder.binding.chipStatus.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(
                    holder.itemView.getContext().getResources().getColor(com.example.hardexpro.R.color.error)));
        }

        // Populate Items
        holder.binding.llItemsContainer.removeAllViews();
        if (transaction.getItemsJson() != null && !transaction.getItemsJson().isEmpty()) {
            java.util.List<com.example.hardexpro.data.model.CartItem> items = new com.google.gson.Gson().fromJson(
                    transaction.getItemsJson(),
                    new com.google.gson.reflect.TypeToken<java.util.List<com.example.hardexpro.data.model.CartItem>>() {
                    }.getType());

            for (com.example.hardexpro.data.model.CartItem item : items) {
                com.example.hardexpro.databinding.ItemHistoryDetailBinding itemBinding = com.example.hardexpro.databinding.ItemHistoryDetailBinding
                        .inflate(
                                android.view.LayoutInflater.from(holder.itemView.getContext()),
                                holder.binding.llItemsContainer,
                                false);
                itemBinding.tvItemName.setText(item.getItem().getName());
                itemBinding.tvItemQty.setText("x" + item.getQuantity());
                itemBinding.tvItemTotal.setText(String.format("₹ %.2f", item.getTotal()));
                holder.binding.llItemsContainer.addView(itemBinding.getRoot());
            }
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onTransactionLongClick(transaction);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemTransactionBinding binding;

        public ViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
