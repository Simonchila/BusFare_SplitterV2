package com.example.busfare_splitterv2.network;

public class PassengerRequest {
    public String name;
    public double surcharge;

    public PassengerRequest(String name, double surcharge) {
        this.name = name;
        this.surcharge = surcharge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(double surcharge) {
        this.surcharge = surcharge;
    }
}
