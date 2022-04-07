package com.example.ui_screens.data;

import java.util.List;

public class Reservation {
    private String date;
    private String time;
    private String message;
    private String restaurant;
    private String restName;
    private String table;
    private String userID;

    public Reservation(String date, String time, String message, String restaurant, String restName, String table, String userID) {
        this.date = date;
        this.time = time;
        this.message = message;
        this.restaurant = restaurant;
        this.restName = restName;
        this.table = table;
        this.userID = userID;

    }

    public String getDate() {return date;}
    public String getTime() {return time;}
    public String getMessage() {return message;}
    public String getRRestaurant() {return restaurant;}
    public String getRRestName() {return restName;}
    public String getTable() {return table;}
    public String getUserID() {return userID;}


}
