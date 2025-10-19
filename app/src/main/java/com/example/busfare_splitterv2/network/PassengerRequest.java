package com.example.busfare_splitterv2.network;

public class PassengerRequest {
    public String name;
    public double surcharge;

    public PassengerRequest(String name, double surcharge) {
        this.name = name;
        this.surcharge = surcharge;
    }
}
