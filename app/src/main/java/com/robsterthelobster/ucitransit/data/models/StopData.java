package com.robsterthelobster.ucitransit.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by robin on 10/16/2017.
 */

public class StopData {

    @SerializedName("rate_limit")
    public int rateLimit;
    @SerializedName("expires_in")
    public int expiresIn;
    @SerializedName("api_latest_version")
    public String apiLatestVersion;
    @SerializedName("generated_on")
    public String generatedOn;
    @SerializedName("data")
    public List<Stop> data = null;
    @SerializedName("api_version")
    public int apiVersion;

}
