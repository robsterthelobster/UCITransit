package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;
import com.robsterthelobster.ucitransit.utils.Constants;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by robin on 10/17/2017.
 */

public class RouteList extends RealmObject{

    @SerializedName(Constants.AGENCY_ID)
    RealmList<Route> routes;

    public RealmList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(RealmList<Route> routes) {
        this.routes = routes;
    }
}
