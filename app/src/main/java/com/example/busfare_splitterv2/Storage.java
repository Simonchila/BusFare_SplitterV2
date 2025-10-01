package com.example.busfare_splitterv2;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private static final String PREFS = "busfare_prefs";
    private static final String KEY_TRIPS = "trips";

    public static void saveTrips(Context ctx, List<Trip> trips) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String json = new Gson().toJson(trips);
        sp.edit().putString(KEY_TRIPS, json).apply();
    }

    public static List<Trip> loadTrips(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String json = sp.getString(KEY_TRIPS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Trip>>(){}.getType();
        return new Gson().fromJson(json, type);
    }
}
