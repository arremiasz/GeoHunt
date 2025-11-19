/**
 * Adapter class that handles the list of accounts searched.
 * @author Nathan Imig
 */
package com.jubair5.geohunt.friends;


import android.content.Context;
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
        setActionButton(holder, friend);
        holder.itemView.setOnClickListener(v -> listener.onFriendClick(friend));

    }

    private void setActionButton(ViewHolder holder, Friend friend) {
        if(friend.getState() == SingleFriendActivity.NOT_FRIENDS_STATE){
            holder.actionButton.setText("Friend");
            //holder.actionButton.setOnClickListener();
        } else if (friend.getState() == SingleFriendActivity.RECEIVED_REQUEST_STATE) {
            holder.actionButton.setText("Accept");
        }
        else if(friend.getState() == SingleFriendActivity.SENT_REQUEST_STATE){
            holder.actionButton.setText("Pending");
        }else{
            holder.actionButton.setText("Challenge");
        }
    }

    private void sendFriendRequest(int userId, int friendID, int state) {
        String friendsURL = ApiConstants.BASE_URL + ApiConstants.Send_Friend_Request_ENDPOINT + "?primaryId=" + userId + "&targetId=" + friendID;

        StringRequest sendRequest = new StringRequest(
                Request.Method.POST,
                friendsURL,
                response -> {
                    Log.d(TAG, "Request Sent");
                    //state = SingleFriendActivity.Send_Friend_Request;

                },
                volleyError -> {
                    Log.e(TAG, "Error Sending Request" + volleyError.toString());
                    String responseBody = "";
                    if(volleyError.networkResponse.data != null) {
                        responseBody = new String(volleyError.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    Log.e(TAG, "Accepting friends error response body: " + responseBody);
                }
        );
        VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(sendRequest);
    }

    private void accpetFriendRequest(int userId, int friendID, int state) {
        String friendsURL = ApiConstants.BASE_URL + ApiConstants.Accept_Friend_Request_ENDPOINT + "?primaryId=" + userId + "&targetId=" + friendID ;

        StringRequest acceptRequest = new StringRequest(
                Request.Method.PUT,
                friendsURL,
                response -> {
                    Log.d(TAG, "Response: "+ response.toString());
                    //state = SingleFriendActivity.ARE_FRIENDS_STATE;

                },
                volleyError -> {
                    Log.e(TAG, "Error accepting request" + volleyError.toString());
                    String responseBody = "";
                    if(volleyError.networkResponse.data != null) {
                        responseBody = new String(volleyError.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    Log.e(TAG, "Accepting friends error response body: " + responseBody);
                }
        );
        VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(acceptRequest);
    }

    private void rejectFriend(int userId, int friendID, int state) {

        String friendsURL = ApiConstants.BASE_URL + ApiConstants.Reject_Friend_Request_ENDPOINT + "?primaryId=" + userId + "&targetId=" + friendID ;

        StringRequest rejectRequest = new StringRequest(
                Request.Method.DELETE,
                friendsURL,
                response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    //state = SingleFriendActivity.NOT_FRIENDS_STATE;

                },
                volleyError -> {
                    Log.e(TAG, "Error removing friends" + volleyError.toString());
                    String responseBody = "";
                    if(volleyError.networkResponse.data != null) {
                        responseBody = new String(volleyError.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    Log.e(TAG, "Accepting friends error response body: " + responseBody);
                }
        );
        VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(rejectRequest);
    }

    private void challengeFriend() {
    }

    private void removeFriend(int userId, int friendID, int state) {
        String friendsURL = ApiConstants.BASE_URL + ApiConstants.Remove_FRIEND + "?primaryId=" + userId + "&targetId=" + friendID ;

        StringRequest removeRequest = new StringRequest(
                Request.Method.DELETE,
                friendsURL,
                response -> {
                    Log.d(TAG, "Response: "+ response);
                    //state = SingleFriendActivity.NOT_FRIENDS_STATE;

                },
                volleyError -> {
                    Log.e(TAG, "Error removing friends " + volleyError.toString());
                    String responseBody = "";
                    if(volleyError.networkResponse.data != null) {
                        responseBody = new String(volleyError.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    Log.e(TAG, "Accepting friends error response body: " + responseBody);
                }
        );
        VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(removeRequest);
    }

    private void setState() {
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
