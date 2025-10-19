package com.example.busfare_splitterv2.network;

import com.example.busfare_splitterv2.UI.PassengerShare;
import java.util.List;

public class TripRequest {
    public String start;
    public String destination;
    public String date;
    public double totalCost;
    public List<PassengerRequest> passengers;

    public TripRequest(String start, String destination, String date, double totalCost, List<PassengerRequest> passengers) {
        this.start = start;
        this.destination = destination;
        this.date = date;
        this.totalCost = totalCost;
        this.passengers = passengers;
    }

    // Getters (optional if using Gson)
    public String getStart() { return start; }
    public String getDestination() { return destination; }
    public String getDate() { return date; }
    public double getTotalCost() { return totalCost; }
    public List<PassengerRequest> getPassengers() { return passengers; }
}
