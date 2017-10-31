package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by robin on 10/16/2017.
 */

public class Prediction extends RealmObject {

    @SerializedName("route_id")
    private Integer routeId;
    @SerializedName("vehicle_id")
    transient private Integer vehicleId;
    @SerializedName("arrival_at")
    private Date arrivalAt;
    @SerializedName("type")
    transient private String type;

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

    public Date getArrivalAt() {
        return arrivalAt;
    }

    public void setArrivalAt(Date arrivalAt) {
        this.arrivalAt = arrivalAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
