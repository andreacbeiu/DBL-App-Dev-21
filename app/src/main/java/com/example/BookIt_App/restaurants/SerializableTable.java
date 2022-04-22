package com.example.BookIt_App.restaurants;

import java.io.Serializable;

public class SerializableTable implements Serializable {
    //This class needs to be passeed onto activities, so it needs to be serializable
    private static final long serialVersionUID = 1L;
    private int seats;

    public SerializableTable(int seats){
        this.seats = seats;
    }

    public int getSeats() {
        return seats;
    }
}
