package com.example.androidexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class HomeFragment extends Fragment {

    private TextView usernameText;
    private String username;
    private Button logoutButton;
    private TextView weatherText;
    private FusedLocationProviderClient fusedLocationClient;

    public HomeFragment(String username) {
        this.username = username;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);
        usernameText = root.findViewById(R.id.main_username_txt);
        usernameText.setText(this.username);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        logoutButton = root.findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        weatherText = root.findViewById(R.id.weather_txt);
        fetchWeather();

        return root;
    }

    /* Fetch weather data from Open-Meteo API */
    private void fetchWeather() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    OkHttpClient client = new OkHttpClient();
                    String url = "https://api.open-meteo.com/v1/forecast?latitude="+location.getLatitude()+"&longitude="+location.getLongitude()+"&current_weather=true";

                    Request request = new Request.Builder().url(url).build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful() && response.body() != null) {
                                final String jsonData = response.body().string();
                                try {
                                    JSONObject json = new JSONObject(jsonData);
                                    JSONObject current = json.getJSONObject("current_weather");
                                    final double temp = (current.getDouble("temperature") * (9.0/5)) + 32; // Convert to Fahrenheit
                                    final double wind = Math.round((current.getDouble("windspeed") / 1.60934) * 100.0) / 100.0; // Convert to mph

                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> {
                                            weatherText.setText("Temp: " + temp + "°F\nWind: " + wind + " mph");
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            });
        }
    }
}