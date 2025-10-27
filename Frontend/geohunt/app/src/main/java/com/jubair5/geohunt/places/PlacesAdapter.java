/**
 * Adapter class that handles the list of places.
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.places;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.jubair5.geohunt.R;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private static final int VIEW_TYPE_ADD = 0;
    private static final int VIEW_TYPE_PLACE = 1;

    private final Context context;
    private final List<Place> places;
    private final OnPlaceClickListener listener;

    public interface OnPlaceClickListener {
        void onAddPlaceClick();
        void onPlaceClick(Place place);
    }

    public PlacesAdapter(Context context, List<Place> places, OnPlaceClickListener listener) {
        this.context = context;
        this.places = places;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ADD) {
            holder.addIcon.setVisibility(View.VISIBLE);
            holder.placeImage.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> listener.onAddPlaceClick());
        } else {
            holder.addIcon.setVisibility(View.GONE);
            holder.placeImage.setVisibility(View.VISIBLE);
            Place place = places.get(position - 1);
            Glide.with(context).load(place.getImageUrl()).into(holder.placeImage);
            holder.itemView.setOnClickListener(v -> listener.onPlaceClick(place));
        }
    }

    @Override
    public int getItemCount() {
        return places.size() + 1; // +1 for the add button
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_ADD;
        }
        return VIEW_TYPE_PLACE;
    }

    /**
     * ViewHolder class for the adapter.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView placeImage;
        ImageView addIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            placeImage = itemView.findViewById(R.id.place_image);
            addIcon = itemView.findViewById(R.id.add_icon);
        }
    }
}
