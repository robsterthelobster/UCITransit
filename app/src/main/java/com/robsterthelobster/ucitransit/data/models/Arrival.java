package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by robin on 10/16/2017.
 */

public class Arrival extends RealmObject {

    @SerializedName("route_id")
    public Integer routeId;
    @SerializedName("vehicle_id")
    public Integer vehicleId;
    @SerializedName("arrival_at")
    public String arrivalAt;
    @SerializedName("type")
    public String type;

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getArrivalAt() {
        return arrivalAt;
    }

    public void setArrivalAt(String arrivalAt) {
        this.arrivalAt = arrivalAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
