package com.example.hardexpro.ui.customer;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hardexpro.data.model.Customer;
import com.example.hardexpro.databinding.ItemCustomerBinding;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customers = new ArrayList<>();
    private final OnCustomerClickListener listener;
    private OnCustomerLongClickListener longClickListener;

    public interface OnCustomerClickListener {
        void onCustomerClick(Customer customer);
    }

    public interface OnCustomerLongClickListener {
        void onCustomerLongClick(Customer customer);
    }

    public CustomerAdapter(OnCustomerClickListener listener) {
        this.listener = listener;
    }

    public void setOnLongClickListener(OnCustomerLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setList(List<Customer> customers) {
        this.customers = customers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomerBinding binding = ItemCustomerBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                false);
        return new CustomerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.binding.tvCustomerName.setText(customer.getName());
        holder.binding.tvCustomerMobile.setText(customer.getMobile());
        holder.binding.tvOutstanding.setText(String.format("₹ %.2f", customer.getOutstandingBalance()));

        holder.itemView.setOnClickListener(v -> listener.onCustomerClick(customer));
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onCustomerLongClick(customer);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        ItemCustomerBinding binding;

        public CustomerViewHolder(ItemCustomerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
