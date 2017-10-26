package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by robin on 10/16/2017.
 */

public class Prediction extends RealmObject {

    @SerializedName("route_id")
    private Integer routeId;
    @SerializedName("vehicle_id")
    private Integer vehicleId;
    @SerializedName("arrival_at")
    private String arrivalAt;
    @SerializedName("type")
    private String type;

    private String arrivalAtSecondary;
    private Route route;
    private Stop stop;

    /*
        boolean checks for statuses
     */
    private boolean isFavorite = false;
    private boolean isCurrent = false;
    private boolean isNearby = true;

    @PrimaryKey
    private String id; // set as routeId + StopId during insertion

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

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
