package com.example.BookIt_App.customers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookIt_App.R;
import com.example.BookIt_App.data.Reservation;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {


    //array for holding restaurant names
    private List<Reservation> reservations = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nametv;
        private final TextView datetv;
        private final TextView timetv;
        private final TextView tabletv;

        public ViewHolder(View view){
            super(view);

            nametv = (TextView) view.findViewById(R.id.tvReservationRestaurantName);
            datetv = (TextView) view.findViewById(R.id.tvReservationDate);
            timetv = (TextView) view.findViewById(R.id.tvReservationTime);
            tabletv = (TextView) view.findViewById(R.id.tvReservationTableNr);
        }

        public TextView getNametv() {
            return nametv;
        }

        public TextView getDatetv() {
            return datetv;
        }

        public TextView getTimetv() {
            return timetv;
        }

        public TextView getTabletv() { return tabletv; }
    }

    public AccountAdapter(FirebaseFirestore db, String userID){
        db.collection("reservation")
                .whereEqualTo("userID", userID)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        this.reservations = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reservation tempReservation = new Reservation(document.getData().get("date").toString(), document.getData().get("time").toString(),
                                    document.getData().get("message").toString(), document.getData().get("restaurant").toString(), document.getData().get("restName").toString(),
                                    document.getData().get("table").toString(), document.getData().get("userID").toString());
                            reservations.add(tempReservation);
                        }
                        this.notifyDataSetChanged();
                    }
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.account_reservation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getNametv().setText(reservations.get(position).getRRestName());
        holder.getDatetv().setText(reservations.get(position).getDate());
        holder.getTimetv().setText(reservations.get(position).getTime());
        holder.getTabletv().setText(reservations.get(position).getTable());
        //holder.itemView.setTag(reservations.get(position).getUserID());
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }
}
