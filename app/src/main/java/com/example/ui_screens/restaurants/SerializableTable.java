package com.example.ui_screens.restaurants;

import java.io.Serializable;

public class SerializableTable implements Serializable {
    private static final long serialVersionUID = 1L;
    private int seats;

    public SerializableTable(int seats){
        this.seats = seats;
    }

    public int getSeats() {
        return seats;
    }
}
