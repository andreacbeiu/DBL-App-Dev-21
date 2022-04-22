package com.example.BookIt_App.data;

public class Table {

    private int seats;
    private boolean seatOccupied;
    private Restaurant restaurant;

    public Table(int seats){
        this.seats = seats;
        seatOccupied = false;
    }

    public void setSeatOccupation(boolean occupation){
        this.seatOccupied = occupation;
    }

    public boolean getSeatOccupation(){
        return this.seatOccupied;
    }

    public void setRestaurant(Restaurant restaurant){
        this.restaurant = restaurant;
    }

    public Restaurant getRestaurant(){
        return this.restaurant;
    }

    public void setSeatNo(int seats){
        this.seats = seats;
    }

    public int getSeats() {
        return seats;
    }
}
