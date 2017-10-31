package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by robin on 10/27/2017.
 */

public class SegmentData{
    @SerializedName("rate_limit")
    private Integer rateLimit;
    @SerializedName("expires_in")
    private Integer expiresIn;
    @SerializedName("api_latest_version")
    private String apiLatestVersion;
    @SerializedName("generated_on")
    private String generatedOn;
    @SerializedName("data")
    private List<Segment> data;
    @SerializedName("api_version")
    private String apiVersion;

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

    public List<Segment> getData() {
        return data;
    }

    public void setData(List<Segment> data) {
        this.data = data;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
