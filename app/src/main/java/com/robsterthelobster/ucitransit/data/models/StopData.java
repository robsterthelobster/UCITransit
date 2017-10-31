package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by robin on 10/16/2017.
 *
 * only need data
 */

public class StopData extends RealmObject{

    @SerializedName("rate_limit")
    transient private Integer rateLimit;
    @SerializedName("expires_in")
    transient private Integer expiresIn;
    @SerializedName("api_latest_version")
    transient private String apiLatestVersion;
    @SerializedName("generated_on")
    transient private String generatedOn;
    @SerializedName("data")
    private RealmList<Stop> data = null;
    @SerializedName("api_version")
    transient private String apiVersion;

    @PrimaryKey
    private int id = 0;

    public Integer getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(Integer rateLimit) {
        this.rateLimit = rateLimit;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getApiLatestVersion() {
        return apiLatestVersion;
    }

    public void setApiLatestVersion(String apiLatestVersion) {
        this.apiLatestVersion = apiLatestVersion;
    }

    public String getGeneratedOn() {
        return generatedOn;
    }

    public void setGeneratedOn(String generatedOn) {
        this.generatedOn = generatedOn;
    }

    public RealmList<Stop> getData() {
        return data;
    }

    public void setData(RealmList<Stop> data) {
        this.data = data;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
