package com.example.androidexample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private String username;
    private TextView usernameLabel;
    private Button deleteButton;
    private View root;

    public ProfileFragment(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.profile_fragment, container, false);

        SharedPreferences prefs = requireActivity().getPreferences(Context.MODE_PRIVATE);
        int savedColor = prefs.getInt("favorite_color", Color.WHITE);
        root.setBackgroundColor(savedColor);

        usernameLabel = root.findViewById(R.id.username_label);
        usernameLabel.setText("@"+username);

        deleteButton = root.findViewById(R.id.delete_account_button);
        // Listener for delete account button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
                builder.setTitle("Account Deletion Confirmation");
                builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.");
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Yes", (v1, v2) -> {
                    getActivity().finish();
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        Button redBtn = root.findViewById(R.id.color_red);
        Button blueBtn = root.findViewById(R.id.color_blue);
        Button greenBtn = root.findViewById(R.id.color_green);

        //Listener for color buttons
        View.OnClickListener listener = v -> {
            int color = Color.WHITE;
            if (v.getId() == R.id.color_red) color = Color.rgb(255, 100, 100);
            else if (v.getId() == R.id.color_blue) color = Color.rgb(100, 100, 255);
            else if (v.getId() == R.id.color_green) color = Color.rgb(100, 255, 100);
            root.setBackgroundColor(color);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("favorite_color", color);
            editor.apply();
        };
        redBtn.setOnClickListener(listener);
        blueBtn.setOnClickListener(listener);
        greenBtn.setOnClickListener(listener);

        return root;
    }
}
