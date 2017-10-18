package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by robin on 10/17/2017.
 */

public class RouteList {
    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    @SerializedName("1039")
    List<Route> routes;
}
