package com.jubair5.geohunt.reward.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;
import com.jubair5.geohunt.places.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ThemeListActivity extends AppCompatActivity implements ThemeAdapter.OnThemeClickListener {


    private static final String TAG = "ThemesList";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";

    private int userId = 12345;



    // Set up for the actual list
    private RecyclerView themesRecycleViewer;
    private Button resetButton;

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
        resetButton = findViewById(R.id.reset_button);

        themesRecycleViewer.setLayoutManager(new LinearLayoutManager(this));

        themeList = new ArrayList<>();
        themeAdapter = new ThemeAdapter(getBaseContext(), themeList, this);

        themesRecycleViewer.setAdapter(themeAdapter);

        getThemes();


    }

    private void getThemes(){
        String searchURL = ApiConstants.BASE_URL + ApiConstants.GET_THEMES_ENDPOINT;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                searchURL,
                null,
                response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    try {
                        themeList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject themeObject = response.getJSONObject(i);
                            themeList.add(new Theme(themeObject));
                        }
                        themeAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing places JSON", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Error getting themes", error);
                });




        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);





    }

    @Override
    public void onThemeClick(Theme theme) {
        if(!theme.isObtained()){

        }
        changeTheme(theme.getName());

    }

    private void changeTheme(String name) {

    }
}
