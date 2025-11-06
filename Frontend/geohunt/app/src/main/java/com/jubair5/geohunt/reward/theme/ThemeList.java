package com.jubair5.geohunt.reward.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.friends.Friend;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import java.util.ArrayList;
import java.util.List;

public class ThemeList extends AppCompatActivity implements ThemeAdapter.OnThemeClickListener {


    private static final String TAG = "ThemesList";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";



    // Set up for the actual list
    private RecyclerView themesRecycleViewer;
    private ThemeAdapter themeAdapter;
    private List<Theme> themeList;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setContentView(R.layout.theme_list_activity);
        prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Theme Customization");
        }

        // Set up Ui elements
        themesRecycleViewer = findViewById(R.id.theme_recycler_view);

        themesRecycleViewer.setLayoutManager(new LinearLayoutManager(this));

        themeList = new ArrayList<>();
        themeAdapter = new ThemeAdapter(getBaseContext(), themeList, this);

        themesRecycleViewer.setAdapter(themeAdapter);


    }

    private void getThemes(){
        String searchURL = ApiConstants.BASE_URL + ApiConstants.GET_THEMES_ENDPOINT;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                searchURL,
                null,
                response -> {
                    Log.d(TAG, "Response: "+ response.toString());

                    // Display Friends
                    themeList.clear();
                    themeList.add(new Theme(response));
                    themeAdapter.notifyDataSetChanged();
                },
                volleyError -> {
                    Log.e(TAG, "Error getting friends", volleyError);
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);





    }

    @Override
    public void onThemeClick(Theme theme) {

    }
}
