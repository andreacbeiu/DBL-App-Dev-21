package com.example.ui_screens.restaurants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class TablesDataHolder implements Serializable {
    private static final long serialVersionUID = 1L;

    public ArrayList<SerializableTable> tables;

    TablesDataHolder(ArrayList<SerializableTable> tables){
        this.tables = tables;
    }
}
