/**
 * Adapter class that handles the list of places.
 * @author Nathan Imig
 */
package com.jubair5.geohunt.friends;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.jubair5.geohunt.R;
import com.jubair5.geohunt.places.Place;
import com.jubair5.geohunt.places.PlacesAdapter;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private final Context context;
    private final List<Friend> friends;

    private final FriendAdapter.OnFriendClickListener listener;


    public interface OnFriendClickListener {
        void onAddPlaceClick();
    }
    public FriendAdapter(Context context, List<Friend> friends, FriendAdapter.OnFriendClickListener listener) {
        this.context = context;
        this.friends = friends;
        this.listener = listener;
    }
    public FriendAdapter(List<Friend> friend) {
        this.friends = friend;
    }





    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return this.friends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendNameTextView;
        ImageView profileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
