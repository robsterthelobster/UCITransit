package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

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
    @PrimaryKey
    private Integer stopId;

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
}
