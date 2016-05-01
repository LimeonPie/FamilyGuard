package com.msc.myplace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tosh8 on 30.04.2016.
 */
public class Location implements Serializable{

    public String name;
    public String id;

    public double lat;
    public double lng;
    public double radius;

    public ArrayList<String> assigned = new ArrayList<>(0);

    public Location() {

    }

    public Location(String name, double lat, double lng, double radius) {
        id = UUID.randomUUID().toString();
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        assigned = new ArrayList<>(0);
    }

    public void addAssigned(String id) {
        assigned.add(id);
    }
}
