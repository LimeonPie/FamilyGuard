package com.msc.myplace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tosh8 on 30.04.2016.
 */
public class Location implements Serializable{

    public String name;

    public double lat;
    public double lng;
    public int radius;

    public List<Member> assigned;

    public Location() {

    }

    public Location(String name, double lat, double lng, int radius) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        assigned = new ArrayList<>(0);
    }
}
