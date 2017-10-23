package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by robin on 10/16/2017.
 */

public class Arrivals extends RealmObject{

    @SerializedName("arrivals")
    public RealmList<Arrival> arrivals = null;
    @SerializedName("agency_id")
    public Integer agencyId;
    @SerializedName("stop_id")
    public Integer stopId;

    public RealmList<Arrival> getArrivals() {
        return arrivals;
    }

    public void setArrivals(RealmList<Arrival> arrivals) {
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
