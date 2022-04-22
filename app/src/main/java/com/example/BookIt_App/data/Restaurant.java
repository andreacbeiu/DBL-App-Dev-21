package com.example.BookIt_App.data;

import com.example.BookIt_App.data.listeners.ImageListener;
import com.example.BookIt_App.data.listeners.MenuListener;
import com.example.BookIt_App.data.listeners.RestaurantListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Restaurant {
    private final String id;
    private String name;
    private List<Table> tables;
    private String description;
    private long rating;

    List<RestaurantListener> restaurantListeners;

    public Restaurant(String id, String name, String description, long rating, List<Table> tables){
        this.id = id;
        this.name = name;
        this.tables = tables;
        this.description = description;
        this.rating = rating;
        for(Table table: tables){
            table.setRestaurant(this);
        }
        restaurantListeners = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public long getRating() {
        return rating;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void addListener(RestaurantListener listener){
        restaurantListeners.add(listener);
    }

    public void notifyDataChanged(){
        for (RestaurantListener listener: restaurantListeners){
            listener.onDataChange(this);
        }
    }

    public static void makeFromId(String id, RestaurantListener listener, OnFailureListener failListener){
        //First, make get data from the database
        FirebaseFirestore
                .getInstance()
                .collection("restaurants")
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        HashMap<String, Object> data = (HashMap<String, Object>)task.getResult().getData();
                        ArrayList<Table> tables = new ArrayList<>();
                        for(HashMap<String, Object> table : (ArrayList<HashMap<String, Object>>) data.get("tables")){ //Convert list of data into tables
                            tables.add(new Table(((Long)table.get("seats")).intValue()));
                        }

                        Restaurant res = new Restaurant(id, //Create new restaurant with data
                                data.get("name").toString(),
                                data.get("description").toString(),
                                ((Double) data.get("rating")).longValue(),
                                tables);
                        res.addListener(listener);
                        res.notifyDataChanged(); //Notify listeners
                    } else {
                        failListener.onFailure(task.getException()); //Notify listener that making of restaurant failed
                    }
                });

    }

    public void getMenuUrl(MenuListener menuListener, OnFailureListener failureListener){
        FirebaseStorage //Get reference to Firebase Storage
                .getInstance()
                .getReference()
                .child("menus/" + this.id + ".pdf") //Menu is stored in menus/{id}.pdf
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    menuListener.onGetMenu(uri.toString());
                })
                .addOnFailureListener(failureListener);
    }

    public void getImageBytes(ImageListener imageListener, OnFailureListener failureListener){
        FirebaseStorage //Get reference to image in Firebase Storage
                .getInstance()
                .getReference()
                .child("images/" + this.id + ".jpg") //Image is stored in images/{id}.jpg
                .getBytes(1024 * 1024 * 5)
                .addOnSuccessListener(bytes -> imageListener.onGetImage(bytes)) //Apply listeners
                .addOnFailureListener(failureListener);
    }

    public void uploadData(OnSuccessListener successListener, OnFailureListener failureListener){
        HashMap<String, Object> data = new HashMap<>(); //Store all data in a HashMap which is accepted by FireBase
        data.put("name", this.name);
        data.put("description", this.description);
        data.put("rating", this.rating);
        data.put("location", null);

        ArrayList<HashMap<String, Object>> tablesList = new ArrayList<>(); //Convert the list of tables into a list of HashMaps so FireBase accepts it
        for(Table table: this.tables){
            HashMap<String, Object> tableMap = new HashMap<>();
            tableMap.put("seats", table.getSeats());
            tableMap.put("occupied", false);
            tablesList.add(tableMap);
        }

        data.put("tables", tablesList);


        FirebaseFirestore //Upload the data to the database
                .getInstance()
                .collection("restaurants")
                .document(this.id)
                .set(data)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);

    }
}
