package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by robin on 10/17/2017.
 */

public class VehicleList extends RealmObject{
    @SerializedName("1039")
    private RealmList<Vehicle> vehicles;

    public RealmList<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(RealmList<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
}
