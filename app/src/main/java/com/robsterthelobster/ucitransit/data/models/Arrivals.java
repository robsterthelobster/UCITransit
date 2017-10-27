package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by robin on 10/16/2017.
 */

public class Arrivals extends RealmObject{

    @SerializedName("arrivals")
    private RealmList<Prediction> arrivals = null;
    @SerializedName("agency_id")
    private Integer agencyId;
    @SerializedName("stop_id")
    private Integer stopId;

    // easy access for Realm
    private String routeId;

    private Route route;
    private Stop stop;

    private boolean isFavorite = false;

    private Date arrivalTime;
    private Date secondaryArrivalTime;

    @PrimaryKey
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<Prediction> getArrivals() {
        return arrivals;
    }

    public void setArrivals(RealmList<Prediction> arrivals) {
        this.arrivals = arrivals;
    }

    public Integer getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Integer agencyId) {
        this.agencyId = agencyId;
    }

    public Integer getStopId() {
        return stopId;
    }

    public void setStopId(Integer stopId) {
        this.stopId = stopId;
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getSecondaryArrivalTime() {
        return secondaryArrivalTime;
    }

    public void setSecondaryArrivalTime(Date secondaryArrivalTime) {
        this.secondaryArrivalTime = secondaryArrivalTime;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}
