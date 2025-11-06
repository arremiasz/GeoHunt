/**
 * Adapter class that handles the list of accounts searched.
 * @author Nathan Imig
 */
package com.jubair5.geohunt.reward.theme;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.jubair5.geohunt.R;
import com.jubair5.geohunt.friends.Friend;

import java.util.List;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ViewHolder> {

    private final Context context;
    private final List<Theme> themes;

    private final OnThemeClickListener listener;


    public interface OnThemeClickListener {
        void onThemeClick(Theme theme);
    }

    public ThemeAdapter(Context context, List<Theme> themes, OnThemeClickListener listener){
        this.context = context;
        this.themes = themes;
        this.listener = listener;
    }



    @NonNull
    @Override
    public ThemeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeAdapter.ViewHolder holder, int position) {
        Theme theme = themes.get(position);

        // Name and state
        holder.friendNameTextView.setText(theme.getName());
        holder.itemView.setOnClickListener(v -> listener.onThemeClick(theme));

    }

    @Override
    public int getItemCount() {
        return this.themes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendNameTextView;
        TextView friendState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friendNameTextView = itemView.findViewById(R.id.username_label);
        }
    }
}
