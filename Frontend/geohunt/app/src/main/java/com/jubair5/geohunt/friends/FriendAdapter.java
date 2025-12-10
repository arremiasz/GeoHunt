/**
 * Adapter class that handles the list of friends and accounts searched
 * @author Nathan Imig
 */
package com.jubair5.geohunt.friends;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.friends.SingleFriendActivity;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private static final String TAG = "FriendsFragment";
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
        setStateText(holder, friend);
        holder.itemView.setOnClickListener(v -> listener.onFriendClick(friend));

    }

    /**
     *  Sets the current status of relationship to the view of the account
     * @param holder
     * @param friend
     */
    @SuppressLint("SetTextI18n")
    private void setStateText(ViewHolder holder, Friend friend) {
        if (friend.getState() == SingleFriendActivity.RECEIVED_REQUEST_STATE) {
            holder.friendState.setText("Recieved");
        }
        else if(friend.getState() == SingleFriendActivity.SENT_REQUEST_STATE){
            holder.friendState.setText("Pending");
        }else if(friend.getState() == SingleFriendActivity.ARE_FRIENDS_STATE){
            holder.friendState.setText("Friends");
        }else{
            holder.friendState.setText("");
        }
    }


    @Override
    public int getItemCount() {
        return this.friends.size();
    }

    /**
     * ViewHolder class for the adapter.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pfp;
        TextView friendNameTextView;
        TextView friendState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friendNameTextView = itemView.findViewById(R.id.username_label);
            friendState = itemView.findViewById(R.id.relationship_State);
            pfp = itemView.findViewById(R.id.friend_pfp);
        }
    }
}
