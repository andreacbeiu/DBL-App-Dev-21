package com.example.BookIt_App.restaurant_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookIt_App.R;
import com.example.BookIt_App.data.Restaurant;
import com.example.BookIt_App.data.Table;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurants);
    }

    //array for holding restaurant names
    private List<Restaurant> restaurants = new ArrayList<>();
    private final OnItemClickListener listener = null;

    //For managing the views in template
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nametv;
        private final TextView descriptiontv;
        private final TextView ratingtv;

        public ViewHolder(View view){
            super(view);

            nametv = (TextView) view.findViewById(R.id.tvRestaurantListName);
            descriptiontv = (TextView) view.findViewById(R.id.tvRestaurantListDescription);
            ratingtv = (TextView) view.findViewById(R.id.tvRestaurantListRating);
        }

        public TextView getNametv() {
            return nametv;
        }

        public TextView getDescriptiontv() {
            return descriptiontv;
        }

        public TextView getRatingtv() {
            return ratingtv;
        }
    }

    public RestaurantListAdapter(FirebaseFirestore db){
        db.collection("restaurants")
                .get() //Get all restaurants in database
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        this.restaurants = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<Table> tables = new ArrayList<>();

                            for(Map<String, Object> entry : (ArrayList<Map<String, Object>>) document.getData().get("tables")) {
                                Table table = new Table(((Long) entry.get("seats")).intValue()); //Convert maps to tables
                                tables.add(table);
                            }

                            //Create new restaurant and add it to the list with data
                            Restaurant tempRestaurant = new Restaurant(document.getId(), document.getData().get("name").toString(),
                                    document.getData().get("description").toString(), ((Double) document.getData().get("rating")).longValue(), tables);
                            restaurants.add(tempRestaurant);
                        }
                        this.notifyDataSetChanged();
                    }
                });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getNametv().setText(restaurants.get(position).getName());
        holder.getDescriptiontv().setText(restaurants.get(position).getDescription());
        holder.getRatingtv().setText(String.valueOf(restaurants.get(position).getRating()));
        holder.itemView.setTag(restaurants.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }
}
