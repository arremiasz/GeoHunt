package com.jubair5.geohunt.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;
import com.jubair5.geohunt.shop.ShopAdapter;
import com.jubair5.geohunt.shop.ShopItem;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for the shop view which allows users to purchase
 * items for future games or profile customizations
 * 
 * @author Alex Remiasz
 */
public class ShopFragment extends Fragment {

    private static final String TAG = "ShopFragment";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_ID = "userId";

    private TextView currentPointsView;
    private RecyclerView shopRecyclerView;
    private ShopAdapter shopAdapter;
    private List<ShopItem> shopItems;
    private int currentPoints;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shop, container, false);

        prefs = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        currentPointsView = root.findViewById(R.id.current_points);
        shopRecyclerView = root.findViewById(R.id.shop_recycler_view);
        shopRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupShopItems();
        fetchShopItems();
        fetchPoints();

        return root;
    }

    /**
     * Sets up the shop items list and adapter.
     */
    private void setupShopItems() {
        shopItems = new ArrayList<>();
        shopAdapter = new ShopAdapter(shopItems, this::onPurchase);
        shopRecyclerView.setAdapter(shopAdapter);
    }

    /**
     * Fetches shop items from the server.
     */
    private void fetchShopItems() {
        String url = ApiConstants.BASE_URL + ApiConstants.GET_SHOP_ITEMS_ENDPOINT;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        shopItems.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject itemJson = response.getJSONObject(i);
                            shopItems.add(new ShopItem(itemJson));
                        }
                        shopAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing shop items", e);
                        Toast.makeText(getContext(), "Error loading shop items.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching shop items", error);
                    Toast.makeText(getContext(), "Failed to load shop. Please try again.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    /**
     * Fetches the user's current points from the server.
     */
    private void fetchPoints() {
        int userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId == -1) {
            Log.e(TAG, "User ID not found in shared preferences.");
            return;
        }

        String url = ApiConstants.BASE_URL + ApiConstants.GET_POINTS_ENDPOINT + "?id=" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        currentPoints = Integer.parseInt(response.trim());
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing points response", e);
                        currentPoints = 0;
                    }
                    updatePointsDisplay();
                },
                error -> Log.e(TAG, "Error fetching points", error));

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void updatePointsDisplay() {
        if (currentPointsView != null) {
            currentPointsView.setText("Current Points: " + currentPoints);
        }
    }

    /**
     * Handles the purchase of an item.
     * 
     * @param item The item being purchased.
     */
    private void onPurchase(ShopItem item) {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Confirm Purchase")
                .setMessage("Are you sure you want to buy " + item.getTitle() + " for " + item.getCost() + " points?")
                .setPositiveButton("Buy", (dialog, which) -> processPurchase(item))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void processPurchase(ShopItem item) {
        if (currentPoints >= item.getCost()) {
            int newPoints = currentPoints - item.getCost();
            updatePointsOnServer(newPoints, item);
        } else {
            Toast.makeText(getContext(), "Not enough points!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the user's points on the server.
     * 
     * @param newPoints The new points balance.
     * @param item      The item purchased (for logging/toast).
     */
    private void updatePointsOnServer(int newPoints, ShopItem item) {
        int userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId == -1)
            return;

        String url = ApiConstants.BASE_URL + ApiConstants.PUT_POINTS_ENDPOINT + "?id=" + userId;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("points", newPoints);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating update points JSON", e);
            return;
        }

        StringRequest request = new StringRequest(Request.Method.PUT, url,
                response -> {
                    Log.d(TAG, "Points updated successfully");
                    fetchPoints();
                    Toast.makeText(getContext(), "Purchased " + item.getTitle() + "!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e(TAG, "Error updating points", error);
                    Toast.makeText(getContext(), "Purchase failed. Please try again.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }
}
