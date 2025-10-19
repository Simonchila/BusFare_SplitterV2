package com.example.busfare_splitterv2.network;

import com.example.busfare_splitterv2.UI.PassengerShare;
import java.io.Serializable;
import java.util.List;

public class TripResponse implements Serializable {
    private int id; // server-side trip ID
    private String route;
    private String date;
    private double totalCost;
    private List<PassengerShare> passengers;

    // No-args constructor for Gson
    public TripResponse() {}

    public TripResponse(int id, String route, String date, double totalCost, List<PassengerShare> passengers) {
        this.id = id;
        this.route = route;
        this.date = date;
        this.totalCost = totalCost;
        this.passengers = passengers;
    }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public List<PassengerShare> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerShare> passengers) { this.passengers = passengers; }
}
