package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by robin on 10/16/2017.
 */

public class StopData {

    @SerializedName("rate_limit")
    public Integer rateLimit;
    @SerializedName("expires_in")
    public Integer expiresIn;
    @SerializedName("api_latest_version")
    public String apiLatestVersion;
    @SerializedName("generated_on")
    public String generatedOn;
    @SerializedName("data")
    public List<Stop> data = null;
    @SerializedName("api_version")
    public String apiVersion;

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

    public List<Stop> getData() {
        return data;
    }

    public void setData(List<Stop> data) {
        this.data = data;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
