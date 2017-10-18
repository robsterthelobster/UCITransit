package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by robin on 10/16/2017.
 */

public class Arrivals extends RealmObject{

    @SerializedName("arrivals")
    public RealmList<Arrival> arrivals = null;
    @SerializedName("agency_id")
    public int agencyId;
    @SerializedName("stop_id")
    public int stopId;

    public RealmList<Arrival> getArrivals() {
        return arrivals;
    }

    public void setArrivals(RealmList<Arrival> arrivals) {
        this.arrivals = arrivals;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }
}
