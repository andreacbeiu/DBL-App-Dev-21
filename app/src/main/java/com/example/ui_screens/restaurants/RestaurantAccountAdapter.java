package com.example.ui_screens.restaurants;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui_screens.R;
import com.example.ui_screens.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantAccountAdapter extends RecyclerView.Adapter<RestaurantAccountAdapter.ViewHolder> {

    private List<User> listUsers = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, email;

        public ViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.nameaccount);
            email = (TextView) itemView.findViewById(R.id.emailaccount);
        }

        public TextView getNameTv() {
            return name;
        }

        public TextView getEmailTv() {
            return email;
        }
    }

    public RestaurantAccountAdapter(FirebaseFirestore db) {
        List<User> tempList = new ArrayList<>();

        CollectionReference users = db.collection("users");
        Query ref = users.whereIn("type", Arrays.asList("employee", "manager"));
        ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString("name");
                        String email_id = document.getString("email");
                        String type = document.getString("type");

                        User tempUser = new User(name, email_id, type);
                        tempList.add(tempUser);
                    }

                    this.listUsers = tempList;
                    this.notifyDataSetChanged();
                }

        });

    }

    @NonNull
    @Override
    public RestaurantAccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_account_items, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAccountAdapter.ViewHolder holder, int position) {
        holder.getNameTv().setText(listUsers.get(position).getName());
        holder.getEmailTv().setText(listUsers.get(position).getEmail());

    }

    @Override
    public int getItemCount() {
        Log.d("BookIT", "users: " + listUsers.size());
        return listUsers.size();
    }
}
