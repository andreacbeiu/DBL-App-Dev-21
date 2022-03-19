package com.example.ui_screens.restaurant_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui_screens.R;
import com.example.ui_screens.data.Restaurant;
import com.example.ui_screens.data.Table;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
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


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view){
            super(view);

            textView = (TextView) view.findViewById(R.id.tvRestaurantListItem);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public RestaurantListAdapter(FirebaseFirestore db){
        db.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        this.restaurants = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<Table> tables = new ArrayList<>();

                            for(Map<String, Object> entry : (ArrayList<Map<String, Object>>) document.getData().get("tables")) {
                                Table table = new Table(((Long) entry.get("seats")).intValue());
                                tables.add(table);
                            }

                            Restaurant tempRestaurant = new Restaurant(document.getId(), document.getData().get("name").toString(), tables);
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
        holder.getTextView().setText(restaurants.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }
}
