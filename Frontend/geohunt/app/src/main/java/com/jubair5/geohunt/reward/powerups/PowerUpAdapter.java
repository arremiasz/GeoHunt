/**
 * Adapter class that handles the list of friends and accounts searched
 * @author Nathan Imig
 */
package com.jubair5.geohunt.reward.powerups;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.jubair5.geohunt.R;
import com.jubair5.geohunt.friends.SingleFriendActivity;

import java.util.List;

public class PowerUpAdapter extends RecyclerView.Adapter<PowerUpAdapter.ViewHolder> {
    private final Context context;
    private final List<PowerUp> powerUps;

    private final OnPowerUpClickListener listener;


    public interface OnPowerUpClickListener {
        void onPowerUpClick(PowerUp powerUp);
    }

    public PowerUpAdapter(Context context, List<PowerUp> powerUps, OnPowerUpClickListener listener){
        this.context = context;
        this.powerUps = powerUps;
        this.listener = listener;
    }



    @NonNull
    @Override
    public PowerUpAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_powerup, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PowerUpAdapter.ViewHolder holder, int position) {
        PowerUp currentPowerUp = powerUps.get(position);

        holder.title.setText(currentPowerUp.getTitle());
        holder.powerUpImage.setImageResource(currentPowerUp.getImage());
        holder.howMany.setText(currentPowerUp.getAmount() + "");
        holder.itemView.setOnClickListener(v -> listener.onPowerUpClick(currentPowerUp));


    }



    @Override
    public int getItemCount() {
        return this.powerUps.size();
    }

    /**
     * ViewHolder class for the adapter.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView powerUpImage;
        TextView title;
        TextView howMany;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            powerUpImage = itemView.findViewById(R.id.powerUp_icon_inventory);
            title = itemView.findViewById(R.id.powerUp_title_inventory);
            howMany = itemView.findViewById(R.id.amount_text);


        }
    }
}
