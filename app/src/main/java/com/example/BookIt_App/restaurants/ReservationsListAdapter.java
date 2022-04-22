package com.example.BookIt_App.restaurants;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.BookIt_App.R;

import java.util.ArrayList;
import java.util.List;

public class ReservationsListAdapter extends RecyclerView.Adapter<ReservationsListAdapter.ViewHolder>{

    private List<Reservation> reservations = new ArrayList<>();

    //Class to hold reservations in the adapter
    public static class Reservation{
        String time, message, table;
        Reservation(String time, String message, String table){
            this.time = time;
            this.message = message;
            this.table = table;
        }

        public String getTime() {
            return time;
        }

        public String getMessage() {
            return message;
        }

        public String getTable() {
            return table;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tabletv;
        private final TextView timetv;
        private final TextView messagetv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tabletv = (TextView) itemView.findViewById(R.id.tvResTableName);
            this.timetv = (TextView) itemView.findViewById(R.id.tvResTime);
            this.messagetv = (TextView) itemView.findViewById(R.id.tvResMessage);
        }

        public TextView getTabletv() {
            return tabletv;
        }

        public TextView getTimetv() {
            return timetv;
        }

        public TextView getMessagetv() {
            return messagetv;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reservation_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.getTabletv().setText("Table " + reservation.table);
        holder.getMessagetv().setText(reservation.message);
        holder.getTimetv().setText(reservation.time);
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public void setReservations(ArrayList<Reservation> reservations){
        this.reservations = reservations;
        this.notifyDataSetChanged();
    }
}
