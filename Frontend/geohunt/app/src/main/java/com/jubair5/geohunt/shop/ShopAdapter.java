/**
 * Adapter class that handles the shop display
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jubair5.geohunt.R;

import java.util.List;

/**
 * Adapter for displaying shop items in a RecyclerView.
 */
public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

    private List<ShopItem> shopItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onPurchaseClick(ShopItem item);
    }

    public ShopAdapter(List<ShopItem> shopItems, OnItemClickListener listener) {
        this.shopItems = shopItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_item_row, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        ShopItem item = shopItems.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.cost.setText("Cost: " + item.getCost() + " pts");
        holder.buyButton.setOnClickListener(v -> listener.onPurchaseClick(item));
    }

    @Override
    public int getItemCount() {
        return shopItems.size();
    }

    static class ShopViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        TextView cost;
        Button buyButton;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.shop_item_title);
            description = itemView.findViewById(R.id.shop_item_description);
            cost = itemView.findViewById(R.id.shop_item_cost);
            buyButton = itemView.findViewById(R.id.shop_item_buy_button);
        }
    }
}
