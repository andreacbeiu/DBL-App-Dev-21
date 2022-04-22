package com.example.BookIt_App.restaurants;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookIt_App.R;
import com.example.BookIt_App.restaurant_list.RestaurantListAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestaurantTablesAdapter extends RecyclerView.Adapter<RestaurantTablesAdapter.ViewHolder> {

    private final String id;
    private List<Table> tables = new ArrayList<>();
    private final RestaurantListAdapter.OnItemClickListener listener = null;

    public void refresh() {
        tables = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurants").document(id).get() //Fetch tables from database
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        //Get results
                        ArrayList<HashMap<String, Object>> result = (ArrayList<HashMap<String, Object>>) task.getResult().getData().get("tables");
                        System.out.println(result.size());
                        int i = 0;
                        for(HashMap<String, Object> data: result){
                            //Convert result to tables
                            Table table = new Table("Table " + i, ((Long) data.get("seats")).intValue());
                            tables.add(table);
                            i++;
                        }
                        //To update the list
                        this.notifyDataSetChanged();
                    }
                });
    }

    //Internal list for use in the list
    public static class Table{
        private final String name;
        private final int seats;

        public Table(String name, int seats){
            this.name = name;
            this.seats = seats;
        }

        public String getName() {
            return name;
        }

        public int getSeats() {
            return seats;
        }
    }

    //Class to hold the views for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nametv;
        private final TextView seatstv;

        public ViewHolder(View view){
            super(view);
            nametv = (TextView) view.findViewById(R.id.tableName);
            seatstv = (TextView) view.findViewById(R.id.tableSeats);
        }

        public TextView getNametv() {
            return nametv;
        }

        public TextView getSeatstv() {
            return seatstv;
        }
    }

    public RestaurantTablesAdapter(String id){
        this.id = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Make viewholder with correct template
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Fill the views with correct data
        holder.getNametv().setText(tables.get(position).getName());
        holder.getSeatstv().setText("seats: " + tables.get(position).getSeats());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    public int getNumSeats(int pos){
        return tables.get(pos).getSeats();
    }

    public List<Table> getTables(){
        return tables;
    }
}
