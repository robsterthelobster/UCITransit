package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;
import com.robsterthelobster.ucitransit.utils.Constants;

import java.util.List;

/**
 * Created by robin on 10/17/2017.
 */

public class RouteList {

    @SerializedName(Constants.AGENCY_ID)
    List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
