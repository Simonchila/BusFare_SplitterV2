package com.example.busfare_splitterv2.UI;

import java.io.Serializable;

public class PassengerShare implements Serializable {
    public String name;
    public double surcharge;
    public double shareAmount; // computed

    public PassengerShare(String name, double surcharge) {
        this.name = name;
        this.surcharge = surcharge;
        this.shareAmount = 0.0;
    }
}
