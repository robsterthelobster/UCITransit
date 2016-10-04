package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by robin on 6/20/2016.
 */
public class Arrivals extends RealmObject{

    int routeId;
    int stopId;
    String stopName;
    String routeColor;

    @SerializedName("PredictionTime")
    private String predictionTime;
    @SerializedName("Predictions")
    private RealmList<Prediction> predictions;

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
