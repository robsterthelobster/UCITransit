package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by robin on 10/16/2017.
 */

public class Route extends RealmObject {


    @SerializedName("description")
    private String description;
    @SerializedName("short_name")
    private String shortName;
    @SerializedName("route_id")
    @PrimaryKey
    private String routeId;
    @SerializedName("url")
    private String url;
    @SerializedName("segments")
    private transient String segments = "";
    @SerializedName("is_active")
    private Boolean isActive;
    @SerializedName("agency_id")
    private Integer agencyId;
    @SerializedName("text_color")
    private String textColor;
    @SerializedName("long_name")
    private String longName;
    @SerializedName("stops")
    private RealmList<String> stopIdList = null;
    @SerializedName("is_hidden")
    private Boolean isHidden;
    @SerializedName("type")
    private String type;
    @SerializedName("color")
    private String color;

    private RealmList<Stop> stopsList;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Integer agencyId) {
        this.agencyId = agencyId;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public RealmList<String> getStopIdList() {
        return stopIdList;
    }

    public void setStopIdList(RealmList<String> stopIdList) {
        this.stopIdList = stopIdList;
    }

    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSegments() {
        return segments;
    }

    public void setSegments(String segments) {
        this.segments = segments;
    }

    public RealmList<Stop> getStops() {
        return stopsList;
    }

    public void setStops(RealmList<Stop> stops) {
        this.stopsList = stops;
    }

    public RealmList<Stop> getStopsList() {
        return stopsList;
    }

    public void setStopsList(RealmList<Stop> stopsList) {
        this.stopsList = stopsList;
    }
}
