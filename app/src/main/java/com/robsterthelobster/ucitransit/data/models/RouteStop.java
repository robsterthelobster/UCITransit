package com.robsterthelobster.ucitransit.data.models;

import com.google.android.gms.ads.InterstitialAd;

import io.realm.RealmObject;

/**
 * Created by robin on 9/14/2016.
 * This will keep track of all the routes and stops
 */
public class RouteStop extends RealmObject{

    private Integer routeID;
    private Integer stopID;

    public Integer getRouteID() {
        return routeID;
    }

    public void setRouteID(Integer routeID) {
        this.routeID = routeID;
    }

    public Integer getStopID() {
        return stopID;
    }

    public void setStopID(Integer stopID) {
        this.stopID = stopID;
    }

    @Override
    public String toString(){
        return "route: " + routeID + ", stop: " + stopID;
    }
}
