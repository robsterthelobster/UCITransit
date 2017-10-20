package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by robin on 10/16/2017.
 */

public class Stop extends RealmObject{

    @SerializedName("code")
    public String code;
    @SerializedName("description")
    public String description;
    @SerializedName("url")
    public String url;
    @SerializedName("parent_station_id")
    public Integer parentStationId;
    @SerializedName("agency_ids")
    public RealmList<String> agencyIds = null;
    @SerializedName("station_id")
    public Integer stationId;
    @SerializedName("location_type")
    public String locationType;
    @SerializedName("location")
    public Coordinate location;
    @SerializedName("stop_id")
    public Integer stopId;
    @SerializedName("routes")
    public RealmList<String> routes = null;
    @SerializedName("name")
    public String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getParentStationId() {
        return parentStationId;
    }

    public void setParentStationId(Integer parentStationId) {
        this.parentStationId = parentStationId;
    }

    public RealmList<String> getAgencyIds() {
        return agencyIds;
    }

    public void setAgencyIds(RealmList<String> agencyIds) {
        this.agencyIds = agencyIds;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public Coordinate getLocation() {
        return location;
    }

    public void setLocation(Coordinate location) {
        this.location = location;
    }

    public Integer getStopId() {
        return stopId;
    }

    public void setStopId(Integer stopId) {
        this.stopId = stopId;
    }

    public RealmList<String> getRoutes() {
        return routes;
    }

    public void setRoutes(RealmList<String> routes) {
        this.routes = routes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
