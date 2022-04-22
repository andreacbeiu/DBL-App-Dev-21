package com.example.BookIt_App.restaurants;

import java.io.Serializable;
import java.util.ArrayList;

public class TablesDataHolder implements Serializable {
    //Serializable holder of a list of tables to be passed onto an activity
    private static final long serialVersionUID = 1L;

    public ArrayList<SerializableTable> tables;

    TablesDataHolder(ArrayList<SerializableTable> tables){
        this.tables = tables;
    }
}
