package com.msc.myplace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tosh8 on 30.04.2016.
 */
public class Member implements Serializable{

    // Basic information
    public String name;
    public String id;

    // Location
    public double lat;
    public double lng;
    public long lastUpdate;

    // Locations
    public ArrayList<Location> locations = new ArrayList<>(0);

    public Member() {

    }

    public Member(String name) {
        id = UUID.randomUUID().toString();
        this.name = name;
        locations = new ArrayList<>(0);
    }

    public void addLocation(Location location) {
        locations.add(location);
    }

}
