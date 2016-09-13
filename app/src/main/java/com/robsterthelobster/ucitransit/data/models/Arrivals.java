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
}
