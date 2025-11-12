/**
 * Adapter class that handles the list of accounts searched.
 * @author Nathan Imig
 */
package com.jubair5.geohunt.friends;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.places.Place;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private final Context context;
    private final List<Friend> friends;

    private final OnFriendClickListener listener;


    public interface OnFriendClickListener {
        void onFriendClick(Friend friend);
    }

    public FriendAdapter(Context context, List<Friend> friends, OnFriendClickListener listener){
        this.context = context;
        this.friends = friends;
        this.listener = listener;
    }



    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.ViewHolder holder, int position) {
        Friend friend = friends.get(position);

        // Name and state
        holder.friendNameTextView.setText(friend.getUsername());
        setActionButton(holder, friend);
        holder.itemView.setOnClickListener(v -> listener.onFriendClick(friend));

    }

    private void setActionButton(ViewHolder holder, Friend friend) {
        if(friend.getState() == 0){
            holder.actionButton.setText("Friend");
        } else if (friend.getState() == 1) {
            holder.actionButton.setText("Accept");
        }
        else if(friend.getState() == 2){
            holder.actionButton.setText("Pending");
        }else{
            holder.actionButton.setText("Challenge");
        }
    }


    @Override
    public int getItemCount() {
        return this.friends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pfp;
        TextView friendNameTextView;
        Button actionButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friendNameTextView = itemView.findViewById(R.id.username_label);
            actionButton = itemView.findViewById(R.id.action_button);
        }
    }
}
