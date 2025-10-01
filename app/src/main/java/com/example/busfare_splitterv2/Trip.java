package com.example.busfare_splitterv2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trip implements Serializable {
    public String route;
    public String date;
    public double totalCost;
    public List<PassengerShare> passengers = new ArrayList<>();

    public Trip(String route, String date, double totalCost) {
        this.route = route;
        this.date = date;
        this.totalCost = totalCost;
    }
}
