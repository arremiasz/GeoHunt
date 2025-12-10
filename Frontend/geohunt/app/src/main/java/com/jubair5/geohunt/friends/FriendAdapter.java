package com.jubair5.geohunt.friends;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.friends.Friend;
import com.jubair5.geohunt.friends.SingleFriendActivity;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Adapter class that handles the list of friends and accounts searched
 * @author Nathan Imig
 */
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
        loadProfilePicture(holder, friend);
        holder.itemView.setOnClickListener(v -> listener.onFriendClick(friend));

    }


    /**
     * Loads the user's profile picture from pfp string.
     * Falls back to the placeholder icon if no profile picture is available.
     */
    private void loadProfilePicture(ViewHolder holder, Friend friend) {
        String pfp = friend.getPhoto();
        if (pfp != null && !pfp.isEmpty()) {
            byte[] decodedImage = Base64.decode(pfp, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
            holder.pfp.setImageBitmap(bitmap);
        } else {
            holder.pfp.setImageResource(android.R.drawable.ic_menu_gallery);
        }
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
            pfp = itemView.findViewById(R.id.friend_pfp);
            friendNameTextView = itemView.findViewById(R.id.username_label);
            friendState = itemView.findViewById(R.id.relationship_State);
        }
    }
}
