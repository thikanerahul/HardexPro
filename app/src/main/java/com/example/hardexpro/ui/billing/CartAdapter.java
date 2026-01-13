package com.example.hardexpro.ui.billing;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hardexpro.data.model.CartItem;
import com.example.hardexpro.databinding.ItemCartBinding;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> cartItems = new ArrayList<>();
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onQuantityChanged();

        void onItemDeleted(int position);
    }

    public void setOnCartChangeListener(OnCartChangeListener listener) {
        this.listener = listener;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    public void addOrUpdateItem(CartItem newItem) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getItem().getId() == newItem.getItem().getId()) {
                cartItems.get(i).setQuantity(cartItems.get(i).getQuantity() + 1);
                notifyItemChanged(i);
                return;
            }
        }
        cartItems.add(newItem);
        notifyItemInserted(cartItems.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding binding = ItemCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.binding.tvItemName.setText(cartItem.getItem().getName());
        holder.binding.tvPricePerUnit.setText(
                String.format("₹ %.2f / %s", cartItem.getItem().getSellingPrice(), cartItem.getItem().getUnit()));
        holder.binding.tvQuantity.setText(String.valueOf((int) cartItem.getQuantity()));
        holder.binding.tvItemTotal.setText(String.format("₹ %.2f", cartItem.getTotal()));

        holder.binding.btnPlus.setOnClickListener(v -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            notifyItemChanged(holder.getAdapterPosition());
            if (listener != null)
                listener.onQuantityChanged();
        });

        holder.binding.btnMinus.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                notifyItemChanged(holder.getAdapterPosition());
                if (listener != null)
                    listener.onQuantityChanged();
            } else {
                int pos = holder.getAdapterPosition();
                cartItems.remove(pos);
                notifyItemRemoved(pos);
                if (listener != null)
                    listener.onItemDeleted(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemCartBinding binding;

        ViewHolder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
