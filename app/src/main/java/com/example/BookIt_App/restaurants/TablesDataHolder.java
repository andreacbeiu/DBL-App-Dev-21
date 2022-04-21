package com.example.BookIt_App.restaurants;

import java.io.Serializable;
import java.util.ArrayList;

public class TablesDataHolder implements Serializable {
    private static final long serialVersionUID = 1L;

    public ArrayList<SerializableTable> tables;

    TablesDataHolder(ArrayList<SerializableTable> tables){
        this.tables = tables;
    }
}
