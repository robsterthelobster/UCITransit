package com.robsterthelobster.ucitransit.data.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by robin on 6/20/2016.
 */
public class Arrivals extends RealmObject{

    @PrimaryKey
    String id;
    int routeId;
    int stopId;
    String routeName;
    String routeColor;
    String stopName;
    boolean isFavorite;
    boolean isCurrent;
    boolean isNearby;

    @SerializedName("PredictionTime")
    private String predictionTime;
    @SerializedName("Predictions")
    private RealmList<Prediction> predictions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return routeColor;
    }

    public void setColor(String color) {
        this.routeColor = color;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorited) {
        isFavorite = favorited;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public boolean isNearby() {
        return isNearby;
    }

    public void setNearby(boolean nearby) {
        isNearby = nearby;
    }

    public String getPredictionTime() {
        return predictionTime;
    }

    public void setPredictionTime(String predictionTime) {
        this.predictionTime = predictionTime;
    }

    public RealmList<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(RealmList<Prediction> predictions) {
        this.predictions = predictions;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getRouteColor() {
        return routeColor;
    }

    public void setRouteColor(String routeColor) {
        this.routeColor = routeColor;
    }
}
